/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.api;

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
import neatlogic.module.dashboard.exception.DashboardAuthenticationException;
import neatlogic.module.dashboard.exception.DashboardNotFoundException;
import com.alibaba.fastjson.JSONObject;
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
            throw new DashboardAuthenticationException("删除");
        }
        dashboardMapper.deleteDashboardVisitCounterByDashboardId(dashboardId);
        dashboardMapper.deleteDashboardDefaultByDashboardId(dashboardId);
        dashboardMapper.deleteDashboardAuthorityByDashboardId(dashboardId);
        dashboardMapper.deleteDashboardById(dashboardId);
        return null;
    }
}
