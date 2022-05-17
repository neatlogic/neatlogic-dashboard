/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dto.AuthenticationInfoVo;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.framework.service.AuthenticationInfoService;
import codedriver.framework.util.TableResultUtil;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class SearchDashboardApi extends PrivateApiComponentBase {

    @Resource
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
        return "查询仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "keyword", type = ApiParamType.STRING, desc = "关键字"),
            @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "是否激活"),
            @Param(name = "searchType", type = ApiParamType.ENUM, rule = "all,system,custom", desc = "类型，all或mine，默认值:all"),
            @Param(name = "currentPage", type = ApiParamType.INTEGER, desc = "当前页数"),
            @Param(name = "pageSize", type = ApiParamType.INTEGER, desc = "每页展示数量 默认20"),
            @Param(name = "needPage", type = ApiParamType.BOOLEAN, desc = "是否分页")})
    @Output({@Param(name = "pageCount", type = ApiParamType.INTEGER, desc = "总页数"),
            @Param(name = "currentPage", type = ApiParamType.INTEGER, desc = "当前页数"),
            @Param(name = "pageSize", type = ApiParamType.INTEGER, desc = "每页展示数量"),
            @Param(name = "tbodyList", explode = DashboardVo[].class, desc = "仪表板列表")
    })
    @Description(desc = "查询仪表板接口")
    @Override
    public Object myDoService(JSONObject jsonObj) {
        DashboardVo dashboardVo = JSONObject.toJavaObject(jsonObj, DashboardVo.class);
        String userUuid = UserContext.get().getUserUuid(true);
        dashboardVo.setFcu(userUuid);
        if (!dashboardVo.getIsAdmin()) {
            AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userUuid);
            dashboardVo.setUserUuid(authenticationInfoVo.getUserUuid());
            dashboardVo.setTeamUuidList(authenticationInfoVo.getTeamUuidList());
            dashboardVo.setRoleUuidList(authenticationInfoVo.getRoleUuidList());
        }
        int rowNum = dashboardMapper.searchDashboardCount(dashboardVo);
        dashboardVo.setRowNum(rowNum);
        List<Long> dashboardIdList = dashboardMapper.searchDashboardId(dashboardVo);

        List<DashboardVo> dashboardList = null;
        if (CollectionUtils.isNotEmpty(dashboardIdList)) {
            dashboardList = dashboardMapper.getDashboardByIdList(dashboardIdList);
        }

        return TableResultUtil.getResult(dashboardList, dashboardVo);
    }
}
