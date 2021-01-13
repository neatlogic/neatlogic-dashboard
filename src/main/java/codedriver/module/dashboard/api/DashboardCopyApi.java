package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.label.NO_AUTH;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.restful.core.constvalue.OperationTypeEnum;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.module.dashboard.exception.DashboardNotFoundException;
import codedriver.module.dashboard.exception.DashboardParamException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@AuthAction(action = NO_AUTH.class)
@OperationType(type = OperationTypeEnum.CREATE)
public class DashboardCopyApi extends PrivateApiComponentBase {

	@Autowired
	private DashboardMapper dashboardMapper;

	@Override
	public String getToken() {
		return "dashboard/copy";
	}

	@Override
	public String getName() {
		return "复制仪表板组件接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ 
		@Param(name = "uuid", type = ApiParamType.STRING, desc = "仪表板uuid", isRequired = true) ,
		@Param(name = "name", type = ApiParamType.STRING, desc = "复制仪表板name", isRequired = true) 
		})
	@Output({ @Param(explode=DashboardVo.class) })
	@Description(desc = "复制仪表板组件接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		DashboardVo dashboardVo = JSONObject.toJavaObject(jsonObj, DashboardVo.class);
		DashboardVo oldDashboardVo = dashboardMapper.getDashboardByUuid(dashboardVo.getUuid());
		if(oldDashboardVo == null) {
			throw new DashboardNotFoundException(dashboardVo.getUuid());
		}
		
		//修改dashboard
		oldDashboardVo.setName(dashboardVo.getName());
		oldDashboardVo.setFcu(UserContext.get().getUserUuid());
		oldDashboardVo.setFcd(null);
		oldDashboardVo.setLcu(null);
		oldDashboardVo.setLcd(null);
		updateWidgetList(oldDashboardVo);
		dashboardMapper.insertDashboard(oldDashboardVo);
		return oldDashboardVo;
		
	}
	/**
	 * 更新组件配置
	 * @param dashboardVo
	 */
	private void updateWidgetList(DashboardVo dashboardVo) {
		List<DashboardWidgetVo> dashboardWidgetList =dashboardMapper.getDashboardWidgetByDashboardUuid(dashboardVo.getUuid());
		dashboardVo.setUuid(null);
		if(CollectionUtils.isNotEmpty(dashboardWidgetList)) {
			for(DashboardWidgetVo widgetVo : dashboardWidgetList) {
				if(StringUtils.isBlank(widgetVo.getChartConfig())) {
					throw new DashboardParamException("widgetList.chartConfig");
				}
				widgetVo.setDashboardUuid(dashboardVo.getUuid());
				widgetVo.setUuid(null);
				dashboardMapper.insertDashboardWidget(widgetVo);
			}
		}
	}
}
