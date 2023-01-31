/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.api;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.dashboard.enums.DashboardType;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.util.RegexUtils;
import neatlogic.module.dashboard.auth.label.DASHBOARD_BASE;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import neatlogic.module.dashboard.exception.DashboardAuthenticationException;
import neatlogic.module.dashboard.exception.DashboardNameExistsException;
import neatlogic.module.dashboard.exception.DashboardNotFoundException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.UPDATE)
public class SaveDashboardApi extends PrivateApiComponentBase {

    @Resource
    private DashboardMapper dashboardMapper;


    @Override
    public String getToken() {
        return "dashboard/save";
    }

    @Override
    public String getName() {
        return "保存仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({@Param(name = "id", type = ApiParamType.LONG, desc = "仪表板uuid，为空代表新增"),
            @Param(name = "name", xss = true, type = ApiParamType.REGEX, rule = RegexUtils.NAME, desc = "仪表板名称", isRequired = true),
            @Param(name = "type", type = ApiParamType.STRING, desc = "分类类型，system|custom 默认custom"),
            @Param(name = "authList", type = ApiParamType.JSONARRAY, desc = "授权列表，如果是system,则必填"),
            @Param(name = "widgetList", type = ApiParamType.JSONARRAY, desc = "组件列表，范例：\"chartType\": \"barchart\"," + "\"h\": 4," + "\"handler\": \"neatlogic.module.process.dashboard.handler.ProcessTaskDashboardHandler\"," + "\"i\": 0," + "\"name\": \"组件1\"," + "\"refreshInterval\": 3," + "\"uuid\": \"aaaa\"," + "\"w\": 5," + "\"x\": 0," + "\"y\": 0")})
    @Output({})
    @Description(desc = "保存仪表板接口")
    @Override
    public Object myDoService(JSONObject jsonObj) {
        Long id = jsonObj.getLong("id");
        DashboardVo dashboardVo = JSONObject.toJavaObject(jsonObj, DashboardVo.class);
        String userUuid = UserContext.get().getUserUuid(true);
        DashboardVo oldDashboardVo = null;
        if (id != null) {
            oldDashboardVo = dashboardMapper.getDashboardById(dashboardVo.getId());
            if (oldDashboardVo == null) {
                throw new DashboardNotFoundException(id);
            }
        }
        if (dashboardMapper.checkDashboardNameIsExists(dashboardVo) > 0) {
            throw new DashboardNameExistsException(dashboardVo.getName());
        }
        if (DashboardType.SYSTEM.getValue().equals(dashboardVo.getType()) || (oldDashboardVo != null && DashboardType.SYSTEM.getValue().equals(oldDashboardVo.getType()))) {
            //判断是否有管理员权限
            if (!AuthActionChecker.check("DASHBOARD_MODIFY")) {
                throw new DashboardAuthenticationException("管理");
            }
        }
        if (id == null) {
            dashboardVo.setFcu(userUuid);
            dashboardMapper.insertDashboard(dashboardVo);
        } else {
            if (DashboardType.CUSTOM.getValue().equals(oldDashboardVo.getType()) && !oldDashboardVo.getFcu().equals(userUuid)) {
                throw new DashboardAuthenticationException("修改");
            }
            dashboardMapper.deleteDashboardAuthorityByDashboardId(id);
            dashboardVo.setLcu(userUuid);
            dashboardMapper.updateDashboard(dashboardVo);
        }
        if (CollectionUtils.isNotEmpty(dashboardVo.getAuthorityList())) {
            dashboardMapper.insertDashboardAuthorityList(dashboardVo.getAuthorityList(), dashboardVo.getId());
        }
        return dashboardVo.getId();
    }

}
