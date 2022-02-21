package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dashboard.dao.mapper.DashboardMapper;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.dto.AuthorityVo;
import codedriver.framework.dto.FieldValidResultVo;
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.IValid;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.auth.label.DASHBOARD_MODIFY;
import codedriver.module.dashboard.exception.DashboardAuthenticationException;
import codedriver.module.dashboard.exception.DashboardNameExistsException;
import codedriver.module.dashboard.exception.DashboardNotFoundException;
import codedriver.module.dashboard.exception.DashboardParamException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.UPDATE)
public class DashboardSaveApi extends PrivateApiComponentBase {

	@Autowired
	private DashboardMapper dashboardMapper;

	@Autowired
	UserMapper userMapper;	
	@Autowired
	RoleMapper roleMapper;
	
	@Override
	public String getToken() {
		return "dashboard/save";
	}

	@Override
	public String getName() {
		return "仪表板修改保存接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({@Param(name = "uuid", type = ApiParamType.STRING, desc = "仪表板uuid，为空代表新增"), 
			@Param(name = "name", xss = true, type = ApiParamType.REGEX, rule = "^[A-Za-z_\\d\\u4e00-\\u9fa5]+$", desc = "仪表板名称", isRequired = true),
			@Param(name = "type", type = ApiParamType.STRING, desc="分类类型，system|custom 默认custom"),
			@Param(name = "valueList", type = ApiParamType.JSONARRAY, desc="授权列表，如果是system,则必填"),
			@Param(name = "widgetList", type = ApiParamType.JSONARRAY, desc = "组件列表，范例：\"chartType\": \"barchart\"," + "\"h\": 4," + "\"handler\": \"codedriver.module.process.dashboard.handler.ProcessTaskDashboardHandler\"," + "\"i\": 0," + "\"name\": \"组件1\"," + "\"refreshInterval\": 3," + "\"uuid\": \"aaaa\"," + "\"w\": 5," + "\"x\": 0," + "\"y\": 0") })
	@Output({  })
	@Description(desc = "仪表板修改保存接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		DashboardVo dashboardVo = JSONObject.toJavaObject(jsonObj, DashboardVo.class);
		String userUuid = UserContext.get().getUserUuid(true);
		if (dashboardMapper.checkDashboardNameIsExists(dashboardVo) > 0) {
			throw new DashboardNameExistsException(dashboardVo.getName());
		}
		if(StringUtils.isBlank(jsonObj.getString("uuid"))) {
			if(DashboardVo.DashBoardType.SYSTEM.getValue().equals(dashboardVo.getType())) {
				//判断是否有管理员权限
				if(CollectionUtils.isEmpty(userMapper.searchUserAllAuthByUserAuth(new UserAuthVo(userUuid, DASHBOARD_MODIFY.class.getSimpleName())))) {
					throw new DashboardAuthenticationException("管理");
				}
				if(CollectionUtils.isEmpty(dashboardVo.getValueList())) {
					throw new DashboardParamException("valueList");
				}
				//更新角色
				for(String value:dashboardVo.getValueList()) {
					AuthorityVo authorityVo = new AuthorityVo();
					if(value.startsWith(GroupSearch.ROLE.getValuePlugin())) {
						authorityVo.setType(GroupSearch.ROLE.getValue());
						authorityVo.setUuid(value.replaceAll(GroupSearch.ROLE.getValuePlugin(), StringUtils.EMPTY));
					}else if(value.startsWith(GroupSearch.USER.getValuePlugin())) {
						authorityVo.setType(GroupSearch.USER.getValue());
						authorityVo.setUuid(value.replaceAll(GroupSearch.USER.getValuePlugin(), StringUtils.EMPTY));
					}else {
						throw new DashboardParamException("valueList");
					}
					dashboardMapper.insertDashboardAuthority(authorityVo,dashboardVo.getUuid());
				}
			}
			dashboardVo.setFcu(userUuid);
			dashboardMapper.insertDashboard(dashboardVo);
			updateWidgetList(dashboardVo);
			return dashboardVo.getUuid();
		}else {
			DashboardVo oldDashboardVo = dashboardMapper.getDashboardByUuid(dashboardVo.getUuid());
			if(oldDashboardVo == null) {
				throw new DashboardNotFoundException(dashboardVo.getUuid());
			}
			if(DashboardVo.DashBoardType.SYSTEM.getValue().equals(oldDashboardVo.getType())) {
				//判断是否有管理员权限
				if(CollectionUtils.isEmpty(userMapper.searchUserAllAuthByUserAuth(new UserAuthVo(userUuid, DASHBOARD_MODIFY.class.getSimpleName())))) {
					throw new DashboardAuthenticationException("管理");
				}
			}else if(!oldDashboardVo.getFcu().equals(userUuid)){
				throw new DashboardAuthenticationException("修改");
			}
			//修改dashboard
			oldDashboardVo.setName(dashboardVo.getName());
			oldDashboardVo.setLcu(userUuid);
			oldDashboardVo.setWidgetList(dashboardVo.getWidgetList());
			dashboardMapper.updateDashboard(oldDashboardVo);
			//更新组件配置
			updateWidgetList(oldDashboardVo);
			return null;
		}
	}
	
	/**
	 * 更新组件配置
	 * @param dashboardVo
	 */
	private void updateWidgetList(DashboardVo dashboardVo) {
		dashboardMapper.deleteDashboardWidgetByDashboardUuid(dashboardVo.getUuid());
		List<DashboardWidgetVo> dashboardWidgetList = dashboardVo.getWidgetList();
		if(CollectionUtils.isNotEmpty(dashboardWidgetList)) {
			for(DashboardWidgetVo widgetVo : dashboardWidgetList) {
				if(StringUtils.isBlank(widgetVo.getChartConfig())) {
					throw new DashboardParamException("widgetList.chartConfig");
				}
				widgetVo.setDashboardUuid(dashboardVo.getUuid());
				dashboardMapper.insertDashboardWidget(widgetVo);
			}
		}
	}

	public IValid name(){
		return value -> {
			DashboardVo dashboardVo = JSON.toJavaObject(value, DashboardVo.class);
			if(dashboardMapper.checkDashboardNameIsExists(dashboardVo) > 0) {
				return new FieldValidResultVo(new DashboardNameExistsException(dashboardVo.getName()));
			}
			return new FieldValidResultVo();
		};
	}
}
