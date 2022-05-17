/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.constvalue.AuthType;
import codedriver.framework.common.constvalue.UserType;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.enums.DashboardType;
import codedriver.framework.dto.AuthenticationInfoVo;
import codedriver.framework.dto.AuthorityVo;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import codedriver.module.dashboard.exception.DashboardAuthenticationException;
import codedriver.module.dashboard.exception.DashboardNotFoundException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class GetDashboardApi extends PrivateApiComponentBase {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TeamMapper teamMapper;

    @Override
    public String getToken() {
        return "dashboard/get";
    }

    @Override
    public String getName() {
        return "获取仪表板详情";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({@Param(name = "id", type = ApiParamType.LONG, desc = "id", isRequired = true)})
    @Output({@Param(explode = DashboardVo.class, desc = "仪表板详细信息")})
    @Description(desc = "获取仪表板详情接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        Long id = jsonObj.getLong("id");
        DashboardVo dashboardVo = dashboardMapper.getDashboardById(id);
        if (dashboardVo == null) {
            throw new DashboardNotFoundException(id);
        }
        if (dashboardVo.getType().equals(DashboardType.SYSTEM.getValue())) {
            if (!dashboardVo.getIsAdmin()) {
                if (CollectionUtils.isNotEmpty(dashboardVo.getAuthorityList())) {
                    List<String> teamUuidList = new ArrayList<>();
                    List<String> roleUuidList = new ArrayList<>();
                    List<String> commonList = new ArrayList<>();
                    for (AuthorityVo auth : dashboardVo.getAuthorityList()) {
                        if (auth.getType().equals(AuthType.TEAM.getValue())) {
                            teamUuidList.add(auth.getUuid());
                        } else if (auth.getType().equals(AuthType.ROLE.getValue())) {
                            roleUuidList.add(auth.getUuid());
                        } else if (auth.getType().equals(AuthType.COMMON.getValue())) {
                            commonList.add(auth.getUuid());
                        }
                    }
                    AuthenticationInfoVo authInfo = UserContext.get().getAuthenticationInfoVo();
                    if (!authInfo.validRole(roleUuidList) && !authInfo.validTeam(teamUuidList) && !commonList.contains(UserType.ALL.getValue()) && !commonList.contains(UserType.LOGIN_USER.getValue())) {
                        throw new DashboardAuthenticationException("查看");
                    }
                }
            }
        } else if (dashboardVo.getType().equals(DashboardType.CUSTOM.getValue()) && !dashboardVo.getFcu().equals(UserContext.get().getUserUuid(true))) {
            throw new DashboardAuthenticationException("查看");
        }
        return dashboardVo;
    }
}
