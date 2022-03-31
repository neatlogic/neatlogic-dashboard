package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.DashboardDefaultVo;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dto.AuthenticationInfoVo;
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.framework.service.AuthenticationInfoService;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.auth.label.DASHBOARD_MODIFY;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Component
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class DashboardSearchApi extends PrivateApiComponentBase {

	@Autowired
	private DashboardMapper dashboardMapper;
	
	@Autowired
	UserMapper userMapper;
	
	@Autowired
	TeamMapper teamMapper;

	@Resource
	private AuthenticationInfoService authenticationInfoService;

	@Override
	public String getToken() {
		return "dashboard/search";
	}

	@Override
	public String getName() {
		return "仪表板查询接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ 
		@Param(name = "keyword", type = ApiParamType.STRING, desc = "关键字"), 
		@Param(name = "type", type = ApiParamType.ENUM, rule = "all,mine", desc = "类型，all或mine，默认值:all"), 
		@Param(name = "currentPage", type = ApiParamType.INTEGER, desc = "当前页数", isRequired = false), 
		@Param(name = "pageSize", type = ApiParamType.INTEGER, desc = "每页展示数量 默认20", isRequired = false), 
		@Param(name = "needPage", type = ApiParamType.BOOLEAN, desc = "是否分页") })
	@Output({ @Param(name = "pageCount", type = ApiParamType.INTEGER, desc = "总页数"), 
		@Param(name = "currentPage", type = ApiParamType.INTEGER, desc = "当前页数"), 
		@Param(name = "pageSize", type = ApiParamType.INTEGER, desc = "每页展示数量"), 
		@Param(name = "dashboardList", explode = DashboardVo[].class, desc = "仪表板列表") 
	})
	@Description(desc = "仪表板查询接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		DashboardVo dashboardVo = jsonObj.toJavaObject(DashboardVo.class);
		String userUuid = UserContext.get().getUserUuid(true);
		dashboardVo.setFcu(userUuid);
		AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userUuid);
		dashboardVo.setUserUuid(authenticationInfoVo.getUserUuid());
		dashboardVo.setTeamUuidList(authenticationInfoVo.getTeamUuidList());
		dashboardVo.setRoleUuidList(authenticationInfoVo.getRoleUuidList());
		int rowNum = dashboardMapper.searchDashboardCount(dashboardVo);
		int pageCount = PageUtil.getPageCount(rowNum, dashboardVo.getPageSize());
		List<String> dashboardUuidList = dashboardMapper.searchAuthorizedDashboardUuid(dashboardVo);
		List<DashboardVo> dashboardList = new ArrayList<DashboardVo>();
		if(CollectionUtils.isNotEmpty(dashboardUuidList)) {
			dashboardList = dashboardMapper.getDashboardListByUuidList(dashboardUuidList);
		}
		List<DashboardDefaultVo> dashboardDefaultList = dashboardMapper.getDefaultDashboardUuidByUserUuid(userUuid);
		List<UserAuthVo> userAuthList = userMapper.searchUserAllAuthByUserAuth(new UserAuthVo(userUuid, DASHBOARD_MODIFY.class.getSimpleName()));
		// 补充权限数据
		for (DashboardVo dashboard : dashboardList) {
			if (CollectionUtils.isNotEmpty(dashboardDefaultList)) {
				if(dashboardDefaultList.stream().anyMatch(d->(d.getDashboardUuid().equals(dashboard.getUuid()) && d.getType().equals(DashboardVo.DashBoardType.SYSTEM.getValue())))){
					dashboard.setIsSystemDefault(1);
				}else if(dashboardDefaultList.stream().anyMatch(d->(d.getDashboardUuid().equals(dashboard.getUuid()) && d.getType().equals(DashboardVo.DashBoardType.CUSTOM.getValue())))){
					dashboard.setIsCustomDefault(1);
				}
			}
			if(dashboard.getType().equals(DashboardVo.DashBoardType.SYSTEM.getValue())
					&& CollectionUtils.isNotEmpty(userAuthList)) {
				dashboard.setIsCanEdit(1);
				dashboard.setIsCanRole(1);
			}else {
				if(userUuid.equalsIgnoreCase(dashboard.getFcu())) {
					dashboard.setIsCanEdit(1);
					if(CollectionUtils.isNotEmpty(userAuthList)) {
						dashboard.setIsCanRole(1);
					}else {
						dashboard.setIsCanRole(0);
					}
				}else {
					dashboard.setIsCanEdit(0);
					dashboard.setIsCanRole(0);
				}
			}
			dashboard.setWidgetList(dashboardMapper.getDashboardWidgetByDashboardUuid(dashboard.getUuid()));
		}
		
		JSONObject returnObj = new JSONObject();
		returnObj.put("rowNum", rowNum);
		returnObj.put("pageCount", pageCount);
		returnObj.put("currentPage", dashboardVo.getCurrentPage());
		returnObj.put("pageSize", dashboardVo.getPageSize());
		returnObj.put("dashboardList", dashboardList);
		return returnObj;
	}
}
