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
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.dashboard.enums.DashboardType;
import neatlogic.framework.restful.annotation.Description;
import neatlogic.framework.restful.annotation.Input;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.module.dashboard.auth.label.DASHBOARD_BASE;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import neatlogic.module.dashboard.exception.DashboardAuthenticationDeleteException;
import neatlogic.module.dashboard.exception.DashboardNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.DELETE)
public class DeleteDashboardApi extends PrivateApiComponentBase {

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
        return "删除仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({@Param(name = "id", type = ApiParamType.LONG, desc = "仪表板id", isRequired = true)})
    @Description(desc = "删除仪表板接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        Long dashboardId = jsonObj.getLong("id");
        DashboardVo dashboardVo = dashboardMapper.getDashboardById(dashboardId);
        if (dashboardVo == null) {
            throw new DashboardNotFoundException(dashboardId);
        }
        if ((dashboardVo.getType().equals(DashboardType.SYSTEM.getValue()) && !AuthActionChecker.check("DASHBOARD_MODIFY"))
                || (dashboardVo.getType().equals(DashboardType.CUSTOM.getValue()) && !dashboardVo.getFcu().equals(UserContext.get().getUserUuid(true)))) {
            throw new DashboardAuthenticationDeleteException();
        }
        dashboardMapper.deleteDashboardVisitCounterByDashboardId(dashboardId);
        dashboardMapper.deleteDashboardDefaultByDashboardId(dashboardId);
        dashboardMapper.deleteDashboardAuthorityByDashboardId(dashboardId);
        dashboardMapper.deleteDashboardById(dashboardId);
        return null;
    }
}
