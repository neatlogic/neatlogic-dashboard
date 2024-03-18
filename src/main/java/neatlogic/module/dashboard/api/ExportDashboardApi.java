/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.dashboard.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import neatlogic.framework.util.FileUtil;
import neatlogic.module.dashboard.auth.label.DASHBOARD_MODIFY;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import neatlogic.module.dashboard.exception.DashboardExportNotFoundException;
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

    @Override
    public String getToken() {
        return "dashboard/export";
    }

    @Override
    public String getName() {
        return "nmda.exportdashboardapi.getname";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "keyword", type = ApiParamType.STRING, desc = "common.keyword"),
            @Param(name = "searchType", type = ApiParamType.ENUM, rule = "all,system,custom", desc = "common.type", help = "all：所有，system：系统面板，custom：个人面板，默认值：all"),
            @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "common.isactive")
    })
    @Description(desc = "nmda.exportdashboardapi.getname")
    @Override
    public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DashboardVo dashboardVo = JSONObject.toJavaObject(paramObj, DashboardVo.class);
        String userUuid = UserContext.get().getUserUuid(true);
        dashboardVo.setFcu(userUuid);
        if (!dashboardVo.getIsAdmin()) {
            AuthenticationInfoVo authenticationInfoVo = UserContext.get().getAuthenticationInfoVo();
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
