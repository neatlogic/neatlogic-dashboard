package codedriver.module.dashboard.api;

import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;

import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.module.dashboard.exception.DashboardWidgetNotFoundException;

@Service
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class DashboardWidgetGetApi extends PrivateApiComponentBase {

	@Autowired
	private DashboardMapper dashboardMapper;

	@Override
	public String getToken() {
		return "dashboard/widget/get";
	}

	@Override
	public String getName() {
		return "获取仪表板组件接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ @Param(name = "uuid", type = ApiParamType.STRING, desc = "仪表板组件uuid", isRequired = true) })
	@Output({ @Param(explode = DashboardWidgetVo.class, type = ApiParamType.JSONOBJECT, desc = "仪表板组件详细信息") })
	@Description(desc = "获取仪表板组件接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		DashboardWidgetVo widgetVo = dashboardMapper.getDashboardWidgetByUuid(jsonObj.getString("uuid"));
		if (widgetVo == null) {
			throw new DashboardWidgetNotFoundException(jsonObj.getString("uuid"));
		}
		return widgetVo;
	}
}
