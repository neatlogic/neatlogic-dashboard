package codedriver.module.dashboard.api;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.module.dashboard.auth.label.DASHBOARD_MODIFY;
import codedriver.module.dashboard.exception.DashboardAuthenticationException;
import codedriver.module.dashboard.exception.DashboardNotFoundException;

@Service
@Transactional
public class DashboardDeleteApi extends ApiComponentBase {

	@Autowired
	private DashboardMapper dashboardMapper;
	
	@Autowired
	UserMapper userMapper;

	@Override
	public String getToken() {
		return "dashboard/delete";
	}

	@Override
	public String getName() {
		return "仪表板删除接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ @Param(name = "uuid", type = ApiParamType.STRING, desc = "仪表板uuid", isRequired = true) })
	@Description(desc = "仪表板删除接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		String dashboardUuid = jsonObj.getString("uuid");
		DashboardVo dashboardVo = dashboardMapper.getDashboardByUuid(dashboardUuid);
		if (dashboardVo == null) {
			throw new DashboardNotFoundException(dashboardUuid);
		}
		String userUuid = UserContext.get().getUserUuid(true);
		List<UserAuthVo> userAuthList = userMapper.searchUserAllAuthByUserAuth(new UserAuthVo(userUuid, DASHBOARD_MODIFY.class.getSimpleName()));
		boolean hasRight = false;
		if (dashboardVo.getType().equals(DashboardVo.DashBoardType.CUSTOM.getValue())&&dashboardVo.getFcu().equals(userUuid)) {
			hasRight = true;
		}
		if (!hasRight&&dashboardVo.getType().equals(DashboardVo.DashBoardType.SYSTEM.getValue())
				&& CollectionUtils.isNotEmpty(userAuthList)) {
				hasRight = true;
		}
		if (!hasRight) {
			throw new DashboardAuthenticationException("管理");
		}
		dashboardMapper.deleteDashboardVisitCounterByDashboardUuid(dashboardUuid);
		dashboardMapper.deleteDashboardWidgetByDashboardUuid(dashboardUuid);
		dashboardMapper.deleteDashboardDefaultByDashboardUuid(dashboardUuid);
		dashboardMapper.deleteDashboardAuthorityByUuid(dashboardUuid);
		dashboardMapper.deleteDashboardByUuid(dashboardUuid);
		return null;
	}
}
