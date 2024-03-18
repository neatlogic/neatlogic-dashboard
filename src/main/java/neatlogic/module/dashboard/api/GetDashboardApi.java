/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.dashboard.api;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.AuthType;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.dashboard.enums.DashboardType;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.AuthorityVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.module.dashboard.auth.label.DASHBOARD_BASE;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import neatlogic.module.dashboard.exception.DashboardAuthenticationReadException;
import neatlogic.module.dashboard.exception.DashboardNotFoundException;
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
                    List<String> userList = new ArrayList<>();
                    for (AuthorityVo auth : dashboardVo.getAuthorityList()) {
                        if (auth.getType().equals(AuthType.TEAM.getValue())) {
                            teamUuidList.add(auth.getUuid());
                        } else if (auth.getType().equals(AuthType.ROLE.getValue())) {
                            roleUuidList.add(auth.getUuid());
                        } else if (auth.getType().equals(AuthType.COMMON.getValue())) {
                            commonList.add(auth.getUuid());
                        } else if (auth.getType().equals(AuthType.USER.getValue())) {
                            userList.add(auth.getUuid());
                        }
                    }
                    AuthenticationInfoVo authInfo = UserContext.get().getAuthenticationInfoVo();
                    if (!authInfo.validUser(userList) && !authInfo.validRole(roleUuidList) && !authInfo.validTeam(teamUuidList) && !commonList.contains(UserType.ALL.getValue()) && !commonList.contains(UserType.LOGIN_USER.getValue())) {
                        throw new DashboardAuthenticationReadException();
                    }
                }
            }
        } else if (dashboardVo.getType().equals(DashboardType.CUSTOM.getValue()) && !dashboardVo.getFcu().equals(UserContext.get().getUserUuid(true))) {
            throw new DashboardAuthenticationReadException();
        }
        return dashboardVo;
    }
}
