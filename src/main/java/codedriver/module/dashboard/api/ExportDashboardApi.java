/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.api;

import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import codedriver.framework.datawarehouse.dto.DataSourceFieldVo;
import codedriver.framework.datawarehouse.dto.DataSourceVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.OperationType;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateBinaryStreamApiComponentBase;
import codedriver.framework.util.FileUtil;
import codedriver.module.dashboard.auth.label.DASHBOARD_MODIFY;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
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
        return "导出仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "keyword", type = ApiParamType.STRING, desc = "关键字"),
            @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "是否激活"),
    })
    @Description(desc = "导出仪表板")
    @Override
    public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DashboardVo dashboardVo = JSONObject.toJavaObject(paramObj, DashboardVo.class);
        // 只导出系统面板
        dashboardVo.setSearchType("system");
        int rowNum = dashboardMapper.searchDashboardCount(dashboardVo);
        dashboardVo.setRowNum(rowNum);
        if (rowNum > 0) {
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
                                Set<Long> datasourceIdSet = new HashSet<>();
                                Set<Long> datasourceFieldIdSet = new HashSet<>();
                                if (CollectionUtils.isNotEmpty(widgetList)) {
                                    for (int j = 0; j < widgetList.size(); j++) {
                                        JSONObject widget = widgetList.getJSONObject(j);
                                        Long datasourceId = widget.getLong("datasourceId");
                                        JSONArray fields = widget.getJSONArray("fields");
                                        if (datasourceId != null) {
                                            datasourceIdSet.add(datasourceId);
                                        }
                                        if (CollectionUtils.isNotEmpty(fields)) {
                                            for (int k = 0; k < fields.size(); k++) {
                                                JSONObject field = fields.getJSONObject(k);
                                                Long datasourceField = field.getLong("datasourceField");
                                                if (datasourceField != null) {
                                                    datasourceFieldIdSet.add(datasourceField);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (datasourceIdSet.size() > 0) {
                                    List<DataSourceVo> dataSourceNameList = dataWarehouseDataSourceMapper.getDataSourceNameListByIdList(new ArrayList<>(datasourceIdSet));
                                    if (dataSourceNameList.size() > 0) {
                                        vo.setDatasourceIdNameMap(dataSourceNameList.stream().collect(Collectors.toMap(DataSourceVo::getId, DataSourceVo::getName)));
                                    }
                                }
                                if (datasourceFieldIdSet.size() > 0) {
                                    List<DataSourceFieldVo> dataSourceFieldNameList = dataWarehouseDataSourceMapper.getDataSourceFieldNameListByIdList(new ArrayList<>(datasourceFieldIdSet));
                                    if (dataSourceFieldNameList.size() > 0) {
                                        vo.setDatasourceFieldIdNameMap(dataSourceFieldNameList.stream().collect(Collectors.toMap(DataSourceFieldVo::getId, DataSourceFieldVo::getName)));
                                    }
                                }
                                zos.putNextEntry(new ZipEntry(vo.getName() + ".json"));
                                zos.write(JSONObject.toJSONBytes(vo));
                                zos.closeEntry();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
