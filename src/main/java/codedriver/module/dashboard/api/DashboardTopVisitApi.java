package codedriver.module.dashboard.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;

@Service
@Transactional
public class DashboardTopVisitApi extends ApiComponentBase {

	@Autowired
	private DashboardMapper dashboardMapper;
	
	@Autowired
	TeamMapper teamMapper;

	@Override
	public String getToken() {
		return "dashboard/topvisit";
	}

	@Override
	public String getName() {
		return "获取最常访问仪表板接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ @Param(name = "limit", type = ApiParamType.INTEGER, desc = "返回数据条数，默认3条") })
	@Output({ @Param(explode = DashboardVo[].class, desc = "仪表板列表") })
	@Description(desc = "获取最常访问仪表板接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		DashboardVo dashboardVo = new DashboardVo();
		String userUuid = UserContext.get().getUserUuid(true);
		dashboardVo.setFcu(userUuid);
		if (jsonObj.containsKey("limit")) {
			dashboardVo.setPageSize(jsonObj.getInteger("limit"));
		} else {
			dashboardVo.setPageSize(3);
		}
		List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
		dashboardVo.setUserUuid(userUuid);
		dashboardVo.setTeamUuidList(teamUuidList);
		dashboardVo.setRoleUuidList(UserContext.get().getRoleUuidList());
		return dashboardMapper.searchTopVisitDashboard(dashboardVo);
	}
}
