package codedriver.module.dashboard.api;

import codedriver.framework.reminder.core.OperationTypeEnum;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dashboard.core.DashboardHandlerFactory;
import codedriver.framework.dashboard.core.IDashboardHandler;
import codedriver.framework.dashboard.dto.DashboardShowConfigVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.module.dashboard.exception.DashboardHandlerNotFoundException;

@Component
@OperationType(type = OperationTypeEnum.SEARCH)
public class DashboardWidgetShowConfigGetApi extends PrivateApiComponentBase {

	@Override
	public String getToken() {
		return "dashboard/widget/showconfig/get";
	}

	@Override
	public String getName() {
		return "获取仪表板展示格式配置接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ 
		@Param(name = "handler", type = ApiParamType.STRING, desc = "仪表板组件数据源", isRequired = true),
		@Param(name = "chartType", type = ApiParamType.STRING, desc = "仪表板组件类型", isRequired = true)
		})
	@Output({ 
		@Param(explode = DashboardShowConfigVo.class, desc = "仪表板展示格式配置")
		})
	@Description(desc = "获取仪表板展示格式配置接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		String handlerStr = jsonObj.getString("handler");
		String chartType = jsonObj.getString("chartType");
		IDashboardHandler handler = DashboardHandlerFactory.getHandler(handlerStr);
		if (handler == null) {
			throw new DashboardHandlerNotFoundException(handlerStr);
		}
		DashboardWidgetVo widgetVo = new DashboardWidgetVo();
		widgetVo.setChartType(chartType);
		widgetVo.setHandler(handlerStr);
		return handler.getConfig(widgetVo);
	}
}
