/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
