package codedriver.module.dashboard.api;

import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.dashboard.handler.DashboardHandlerFactory;
import codedriver.framework.dashboard.handler.IDashboardHandler;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.exception.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
@Component
public class DashboardWidgetDataGetApi extends PrivateApiComponentBase {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public String getToken() {
        return "dashboard/widget/data/get";
    }

    @Override
    public String getName() {
        return "获取仪表板组件数据接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "uuid", type = ApiParamType.STRING, desc = "仪表板组件uuid"),
            @Param(name = "chartType", type = ApiParamType.STRING, desc = "组件图表类型"),
            @Param(name = "conditionConfig", type = ApiParamType.STRING, desc = "数据过滤"),
            @Param(name = "handler", type = ApiParamType.STRING, desc = "组件处理类"),
            @Param(name = "chartConfig", type = ApiParamType.STRING, desc = "显示格式")
    })
    @Output({@Param(explode = ChartDataVo.class, desc = "数据集")})
    @Description(desc = "获取仪表板组件数据接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        String uuid = jsonObj.getString("uuid");
        DashboardWidgetVo widgetVo = null;
        if (StringUtils.isNotBlank(uuid)) {
            widgetVo = dashboardMapper.getDashboardWidgetByUuid(uuid);
            if (widgetVo == null) {
                throw new DashboardWidgetNotFoundException(jsonObj.getString("uuid"));
            }
        } else {
            if (StringUtils.isBlank(jsonObj.getString("chartType"))) {
                throw new DashboardParamException("chartType");
            }
            if (StringUtils.isBlank(jsonObj.getString("conditionConfig"))) {
                throw new DashboardParamException("conditionConfig");
            }
            if (StringUtils.isBlank(jsonObj.getString("handler"))) {
                throw new DashboardParamException("handler");
            }
            if (StringUtils.isBlank(jsonObj.getString("chartConfig"))) {
                throw new DashboardParamException("chartConfig");
            }
            validChartConfig(jsonObj.getJSONObject("chartConfig"));
            widgetVo = JSONObject.toJavaObject(jsonObj, DashboardWidgetVo.class);
        }
        IDashboardHandler handler = DashboardHandlerFactory.getHandler(widgetVo.getHandler());
        if (handler == null) {
            throw new DashboardHandlerNotFoundException(widgetVo.getHandler());
        }
        widgetVo.setHandler(handler.getName());
        ChartDataVo chartDataVo = handler.getData(widgetVo);
        JSONObject chartDataJson = JSONObject.parseObject(JSONObject.toJSONString(chartDataVo));
        JSONObject dataJson = chartDataJson.getJSONObject("data");
        chartDataJson.remove("data");
        chartDataJson.put("theadList", dataJson.get("theadList"));
        chartDataJson.put("columnList", dataJson.get("columnList"));
        chartDataJson.put("dataList", dataJson.get("dataList"));
        chartDataJson.put("total", dataJson.get("total"));
        return chartDataJson;
    }

    /**
     * 校验chartConfig 必填项
     *
     * @param chartConfigJson chartConfig 入参
     */
    private void validChartConfig(JSONObject chartConfigJson) {
        if (StringUtils.isBlank(chartConfigJson.getString("aggregate"))) {
            throw new DashboardStatisticsParamException();
        }
        if (StringUtils.isBlank(chartConfigJson.getString("groupfield"))) {
            throw new DashboardGroupFieldParamException();
        }
        if (StringUtils.isBlank(chartConfigJson.getString("maxgroup"))) {
            throw new DashboardLimitCountParamException();
        }
    }
}
