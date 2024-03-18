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
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.common.constvalue.ApiParamType;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.DELETE)
public class DeleteDashboardApi extends PrivateApiComponentBase {

    @Resource
    private DashboardMapper dashboardMapper;


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
