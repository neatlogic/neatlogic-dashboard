/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.api;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.restful.annotation.Description;
import neatlogic.framework.restful.annotation.Input;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateBinaryStreamApiComponentBase;
import neatlogic.framework.service.AuthenticationInfoService;
import neatlogic.framework.util.FileUtil;
import neatlogic.module.dashboard.auth.label.DASHBOARD_MODIFY;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import neatlogic.module.dashboard.exception.DashboardExportNotFoundException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AuthAction(action = DASHBOARD_MODIFY.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class ExportDashboardApi extends PrivateBinaryStreamApiComponentBase {

    @Resource
    private DashboardMapper dashboardMapper;

    @Resource
    DataWarehouseDataSourceMapper dataWarehouseDataSourceMapper;

    @Resource
    private AuthenticationInfoService authenticationInfoService;

    @Override
    public String getToken() {
        return "dashboard/export";
    }

    @Override
    public String getName() {
        return "导出仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "keyword", type = ApiParamType.STRING, desc = "关键字"),
            @Param(name = "searchType", type = ApiParamType.ENUM, rule = "all,system,custom", desc = "类型，all或mine，默认值:all"),
            @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "是否激活")
    })
    @Description(desc = "导出仪表板")
    @Override
    public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DashboardVo dashboardVo = JSONObject.toJavaObject(paramObj, DashboardVo.class);
        String userUuid = UserContext.get().getUserUuid(true);
        dashboardVo.setFcu(userUuid);
        if (!dashboardVo.getIsAdmin()) {
            AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userUuid);
            dashboardVo.setUserUuid(authenticationInfoVo.getUserUuid());
            dashboardVo.setTeamUuidList(authenticationInfoVo.getTeamUuidList());
            dashboardVo.setRoleUuidList(authenticationInfoVo.getRoleUuidList());
        }
        int rowNum = dashboardMapper.searchDashboardCount(dashboardVo);
        dashboardVo.setRowNum(rowNum);
        if (rowNum > 0) {
            Map<Long, DataSourceVo> datasourceMap = new HashMap<>();
            String fileName = FileUtil.getEncodedFileName("仪表板." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pak");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", " attachment; filename=\"" + fileName + "\"");
            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                for (int i = 1; i <= dashboardVo.getPageCount(); i++) {
                    dashboardVo.setCurrentPage(i);
                    List<Long> dashboardIdList = dashboardMapper.searchDashboardId(dashboardVo);
                    if (!dashboardIdList.isEmpty()) {
                        List<DashboardVo> dashboardList = dashboardMapper.getDashboardByIdList(dashboardIdList);
                        if (!dashboardList.isEmpty()) {
                            for (DashboardVo vo : dashboardList) {
                                // 查询每个图表的数据源和字段名称，以便导入时根据名称还原数据源配置
                                JSONArray widgetList = vo.getWidgetList();
                                JSONArray datasourceInfoList = new JSONArray();
                                if (CollectionUtils.isNotEmpty(widgetList)) {
                                    for (int j = 0; j < widgetList.size(); j++) {
                                        JSONObject widget = widgetList.getJSONObject(j);
                                        Long datasourceId = widget.getLong("datasourceId");
                                        JSONArray fields = widget.getJSONArray("fields");
                                        JSONArray conditionList = widget.getJSONArray("conditionList");
                                        if (datasourceId != null) {
                                            JSONObject config = new JSONObject();
                                            config.put("widgetUuid", widget.getString("uuid"));
                                            config.put("id", datasourceId);
                                            DataSourceVo datasource = datasourceMap.get(datasourceId);
                                            if (datasource == null) {
                                                datasource = dataWarehouseDataSourceMapper.getDataSourceNameAndFieldNameListById(datasourceId);
                                                if (datasource == null) {
                                                    continue;
                                                }
                                                datasourceMap.put(datasourceId, datasource);
                                            }
                                            config.put("name", datasource.getName());
                                            List<DataSourceFieldVo> fieldList = datasource.getFieldList();
                                            if (CollectionUtils.isNotEmpty(fieldList)) {
                                                if (CollectionUtils.isNotEmpty(fields)) {
                                                    JSONArray fieldArray = new JSONArray();
                                                    for (int k = 0; k < fields.size(); k++) {
                                                        JSONObject field = fields.getJSONObject(k);
                                                        Optional<DataSourceFieldVo> opt = fieldList.stream().filter(o -> Objects.equals(o.getId(), field.getLong("datasourceField"))).findFirst();
                                                        opt.ifPresent(dataSourceFieldVo -> fieldArray.add(new JSONObject() {
                                                            {
                                                                this.put("id", dataSourceFieldVo.getId());
                                                                this.put("name", dataSourceFieldVo.getName());
                                                            }
                                                        }));
                                                    }
                                                    config.put("fieldList", fieldArray);
                                                }
                                                if (CollectionUtils.isNotEmpty(conditionList)) {
                                                    JSONArray conditionFieldArray = new JSONArray();
                                                    for (int k = 0; k < conditionList.size(); k++) {
                                                        JSONObject field = conditionList.getJSONObject(k);
                                                        Optional<DataSourceFieldVo> opt = fieldList.stream().filter(o -> Objects.equals(o.getId(), field.getLong("id"))).findFirst();
                                                        opt.ifPresent(dataSourceFieldVo -> conditionFieldArray.add(new JSONObject() {
                                                            {
                                                                this.put("id", dataSourceFieldVo.getId());
                                                                this.put("name", dataSourceFieldVo.getName());
                                                            }
                                                        }));
                                                    }
                                                    config.put("conditionList", conditionFieldArray);
                                                }
                                            }
                                            datasourceInfoList.add(config);
                                        }
                                    }
                                }
                                vo.setDatasourceInfoList(datasourceInfoList);
                                zos.putNextEntry(new ZipEntry(vo.getName() + ".json"));
                                zos.write(JSONObject.toJSONBytes(vo));
                                zos.closeEntry();
                            }
                        }
                    }
                }
            }
        } else {
            throw new DashboardExportNotFoundException();
        }
        return null;
    }

}