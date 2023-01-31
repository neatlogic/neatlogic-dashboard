/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.api;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.dto.AuthorityVo;
import neatlogic.framework.exception.file.FileExtNotAllowedException;
import neatlogic.framework.exception.file.FileNotUploadException;
import neatlogic.framework.restful.annotation.Description;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.annotation.Output;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateBinaryStreamApiComponentBase;
import neatlogic.framework.transaction.util.TransactionUtil;
import neatlogic.module.dashboard.auth.label.DASHBOARD_MODIFY;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@Service
@AuthAction(action = DASHBOARD_MODIFY.class)
@OperationType(type = OperationTypeEnum.OPERATE)
public class ImportDashboardApi extends PrivateBinaryStreamApiComponentBase {
    static Logger logger = LoggerFactory.getLogger(ImportDashboardApi.class);
    @Resource
    DashboardMapper dashboardMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TeamMapper teamMapper;

    @Resource
    RoleMapper roleMapper;

    @Resource
    DataWarehouseDataSourceMapper dataWarehouseDataSourceMapper;

    @Override
    public String getToken() {
        return "dashboard/import";
    }

    @Override
    public String getName() {
        return "导入仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({
            @Param(name = "successCount", type = ApiParamType.INTEGER, desc = "导入成功数量"),
            @Param(name = "failureCount", type = ApiParamType.INTEGER, desc = "导入失败数量"),
            @Param(name = "failureReasonList", type = ApiParamType.JSONARRAY, desc = "失败原因")
    })
    @Description(desc = "导入仪表板")
    @Override
    public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject resultObj = new JSONObject();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> multipartFileMap = multipartRequest.getFileMap();
        if (multipartFileMap.isEmpty()) {
            throw new FileNotUploadException();
        }
        JSONArray resultList = new JSONArray();
        byte[] buf = new byte[1024];
        int successCount = 0;
        int failureCount = 0;
        for (Map.Entry<String, MultipartFile> entry : multipartFileMap.entrySet()) {
            MultipartFile multipartFile = entry.getValue();
            try (ZipInputStream zis = new ZipInputStream(multipartFile.getInputStream());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                while (zis.getNextEntry() != null) {
                    int len;
                    while ((len = zis.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    DashboardVo dashboardVo = JSONObject.parseObject(new String(out.toByteArray(), StandardCharsets.UTF_8), new TypeReference<DashboardVo>() {
                    });
                    JSONObject result = null;
                    TransactionStatus tx = TransactionUtil.openTx();
                    try {
                        result = save(dashboardVo);
                        TransactionUtil.commitTx(tx);
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                        TransactionUtil.rollbackTx(tx);
                        throw new Exception(ex.getMessage(), ex);
                    }
                    if (MapUtils.isNotEmpty(result)) {
                        resultList.add(result);
                        failureCount++;
                    } else {
                        successCount++;
                    }
                    out.reset();
                }
            } catch (Exception e) {
                throw new FileExtNotAllowedException(multipartFile.getOriginalFilename());
            }
        }
        resultObj.put("successCount", successCount);
        resultObj.put("failureCount", failureCount);
        if (CollectionUtils.isNotEmpty(resultList)) {
            resultObj.put("failureReasonList", resultList);
        }
        return resultObj;
    }

    private JSONObject save(DashboardVo dashboardVo) {
        List<String> failReasonList = new ArrayList<>();
        String name = dashboardVo.getName();
        JSONArray widgetList = dashboardVo.getWidgetList();
        JSONArray datasourceInfoList = dashboardVo.getDatasourceInfoList();
        /*
        datasourceInfoList中存放了压缩包中dashboard的数据源（包含字段、条件字段）id与名称信息，需要根据这些信息还原到被导入的目标系统中的数据源信息
        一旦某个图表有问题，跳过整个dashboard
         */
        Map<String, JSONObject> datasourceInfoMap = new HashMap<>();
        Map<String, DataSourceVo> allDataSourceVoMap = new HashMap<>();
        List<DataSourceVo> allDatasource = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(datasourceInfoList)) {
            for (int j = 0; j < datasourceInfoList.size(); j++) {
                JSONObject obj = datasourceInfoList.getJSONObject(j);
                datasourceInfoMap.put(obj.getString("widgetUuid"), obj);
            }
            Set<String> allDatasourceName = new HashSet<>();
            Set<String> finalAllDatasourceName = allDatasourceName;
            datasourceInfoList.forEach(o -> finalAllDatasourceName.add(((JSONObject) o).getString("name")));
            allDatasource = dataWarehouseDataSourceMapper.getDataSourceListByNameList(new ArrayList<>(allDatasourceName));
            List<DataSourceVo> finalAllDatasource = allDatasource;
            allDatasourceName = allDatasourceName.stream().filter(o -> finalAllDatasource.stream().noneMatch(e -> Objects.equals(o, e.getName()))).collect(Collectors.toSet());
            if (allDatasourceName.size() != 0 ) {
                failReasonList.add("不存在的数据源：" + String.join("、", allDatasourceName));
            }
            allDataSourceVoMap = allDatasource.stream().collect(Collectors.toMap(DataSourceVo::getName, o -> o));
        }
        if (CollectionUtils.isNotEmpty(widgetList)) {
            JSONArray newWidgetList = new JSONArray();
            dashboardVo.setWidgetList(newWidgetList);
            for (int i = 0; i < widgetList.size(); i++) {
                JSONObject widget = widgetList.getJSONObject(i);
                String uuid = widget.getString("uuid");
                String dataType = widget.getString("dataType");
                if (allDatasource.size() > 0 && datasourceInfoMap.containsKey(uuid)) {
                    JSONObject datasourceConfig = datasourceInfoMap.get(uuid);
                    if ("dynamic".equals(dataType) && datasourceConfig == null) {
                        failReasonList.add(widget.getString("name") + "未配置数据源");
                        break;
                    }
                    String datasourceName = datasourceConfig.getString("name"); // 数据源名称
                    JSONArray fieldIdNameList = datasourceConfig.getJSONArray("fieldList"); // 数据源字段名称与旧ID
                    JSONArray conditionIdNameList = datasourceConfig.getJSONArray("conditionList"); // 条件字段名称与旧ID
                    DataSourceVo dataSourceVo = allDataSourceVoMap.get(datasourceName);
                    if (dataSourceVo == null) {
                        //failReasonList.add("不存在的数据源：" + datasourceName);
                        break;
                    }
                    widget.put("datasourceId", dataSourceVo.getId());
                    List<DataSourceFieldVo> allField = dataSourceVo.getFieldList();
                    if (CollectionUtils.isEmpty(allField)) {
                        failReasonList.add("数据源：" + datasourceName + "无任何字段");
                    } else {
                        Map<String, Long> allFieldNameIdMap = allField.stream().collect(Collectors.toMap(DataSourceFieldVo::getName, DataSourceFieldVo::getId));
                        Set<String> allUnknownFieldNames = new HashSet<>();
                        if (CollectionUtils.isNotEmpty(fieldIdNameList)) {
                            Map<Long, String> oldFieldIdNameMap = new HashMap<>();
                            for (int j = 0; j < fieldIdNameList.size(); j++) {
                                JSONObject field = fieldIdNameList.getJSONObject(j);
                                oldFieldIdNameMap.put(field.getLong("id"), field.getString("name"));
                            }
                            JSONArray fields = widget.getJSONArray("fields");
                            JSONArray newFields = new JSONArray();
                            Set<String> unknownFieldNames = new HashSet<>();
                            for (int j = 0; j < fields.size(); j++) {
                                JSONObject field = fields.getJSONObject(j);
                                Long datasourceField = field.getLong("datasourceField");
                                String fieldName = oldFieldIdNameMap.get(datasourceField);
                                Long newFieldId = allFieldNameIdMap.get(fieldName);
                                if (newFieldId == null) {
                                    unknownFieldNames.add(fieldName);
                                    continue;
                                }
                                field.put("datasourceField", newFieldId);
                                newFields.add(field);
                            }
                            if (unknownFieldNames.size() == 0) {
                                widget.put("fields", newFields);
                            } else {
                                allUnknownFieldNames.addAll(unknownFieldNames);
                            }
                        }
                        if (CollectionUtils.isNotEmpty(conditionIdNameList)) {
                            Map<Long, String> oldFieldIdNameMap = new HashMap<>();
                            for (int j = 0; j < conditionIdNameList.size(); j++) {
                                JSONObject field = conditionIdNameList.getJSONObject(j);
                                oldFieldIdNameMap.put(field.getLong("id"), field.getString("name"));
                            }
                            JSONArray oldConditionList = widget.getJSONArray("conditionList");
                            JSONArray newConditionList = new JSONArray();
                            Set<String> unknownFieldNames = new HashSet<>();
                            for (int j = 0; j < oldConditionList.size(); j++) {
                                JSONObject condition = oldConditionList.getJSONObject(j);
                                Long datasourceField = condition.getLong("id");
                                String fieldName = oldFieldIdNameMap.get(datasourceField);
                                Long newFieldId = allFieldNameIdMap.get(fieldName);
                                if (newFieldId == null) {
                                    unknownFieldNames.add(fieldName);
                                    continue;
                                }
                                condition.put("id", newFieldId);
                                newConditionList.add(condition);
                            }
                            if (unknownFieldNames.size() == 0) {
                                widget.put("conditionList", newConditionList);
                            } else {
                                allUnknownFieldNames.addAll(unknownFieldNames);
                            }
                        }
                        if (allUnknownFieldNames.size() > 0) {
                            failReasonList.add("数据源：" + datasourceName + "中不存在字段：" + String.join("、", allUnknownFieldNames));
                        }
                    }
                }
                newWidgetList.add(widget);
            }
        } else {
            throw new FileExtNotAllowedException(dashboardVo.getName());
        }
        if (failReasonList.size() == 0) {
            dashboardVo.setId(null);
            DashboardVo oldDashboardVo = dashboardMapper.getSystemDashBoardByName(name);
            if (oldDashboardVo == null) {
                dashboardVo.setFcu(UserContext.get().getUserUuid());
                dashboardMapper.insertDashboard(dashboardVo);
            } else {
                dashboardVo.setId(oldDashboardVo.getId());
                dashboardVo.setLcu(UserContext.get().getUserUuid());
                dashboardMapper.updateDashboard(dashboardVo);
            }
            List<AuthorityVo> authorityList = dashboardVo.getAuthorityList();
            if (CollectionUtils.isNotEmpty(authorityList)) {
                List<String> userUuidList = authorityList.stream().filter(o -> GroupSearch.USER.getValue().equals(o.getType())).map(AuthorityVo::getUuid).collect(Collectors.toList());
                List<String> teamUuidList = authorityList.stream().filter(o -> GroupSearch.TEAM.getValue().equals(o.getType())).map(AuthorityVo::getUuid).collect(Collectors.toList());
                List<String> roleUuidList = authorityList.stream().filter(o -> GroupSearch.ROLE.getValue().equals(o.getType())).map(AuthorityVo::getUuid).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userUuidList)) {
                    userUuidList = userMapper.getUserUuidListByUuidListAndIsActive(userUuidList, null);
                }
                if (CollectionUtils.isNotEmpty(teamUuidList)) {
                    teamUuidList = teamMapper.getTeamUuidListByUuidList(teamUuidList);
                }
                if (CollectionUtils.isNotEmpty(roleUuidList)) {
                    roleUuidList = roleMapper.getRoleUuidListByUuidList(roleUuidList);
                }
                authorityList.clear();
                if (CollectionUtils.isNotEmpty(userUuidList)) {
                    userUuidList.forEach(o -> authorityList.add(new AuthorityVo(GroupSearch.USER.getValue(), o)));
                }
                if (CollectionUtils.isNotEmpty(teamUuidList)) {
                    teamUuidList.forEach(o -> authorityList.add(new AuthorityVo(GroupSearch.TEAM.getValue(), o)));
                }
                if (CollectionUtils.isNotEmpty(roleUuidList)) {
                    roleUuidList.forEach(o -> authorityList.add(new AuthorityVo(GroupSearch.ROLE.getValue(), o)));
                }
                if (CollectionUtils.isNotEmpty(authorityList)) {
                    dashboardMapper.deleteDashboardAuthorityByDashboardId(dashboardVo.getId());
                    dashboardMapper.insertDashboardAuthorityList(authorityList, dashboardVo.getId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(failReasonList)) {
            JSONObject result = new JSONObject();
            result.put("item", "导入：" + name + "时出现如下问题：");
            result.put("list", failReasonList);
            return result;
        }
        return null;
    }

}
