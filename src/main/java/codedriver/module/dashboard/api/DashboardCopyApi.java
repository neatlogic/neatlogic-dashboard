package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.core.AuthActionChecker;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.dto.AuthorityVo;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.auth.label.DASHBOARD_MODIFY;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import codedriver.module.dashboard.exception.DashboardNotFoundException;
import codedriver.module.dashboard.exception.DashboardParamException;
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
@OperationType(type = OperationTypeEnum.CREATE)
public class DashboardCopyApi extends PrivateApiComponentBase {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public String getToken() {
        return "dashboard/copy";
    }

    @Override
    public String getName() {
        return "复制仪表板组件";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "uuid", type = ApiParamType.STRING, desc = "仪表板uuid", isRequired = true),
            @Param(name = "name", type = ApiParamType.STRING, desc = "复制仪表板name", isRequired = true)
    })
    @Output({@Param(explode = DashboardVo.class)})
    @Description(desc = "复制仪表板组件接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        DashboardVo paramDashboardVo = JSONObject.toJavaObject(jsonObj, DashboardVo.class);
        String paramDashboardVoUid = paramDashboardVo.getUuid();
        DashboardVo oldDashboardVo = dashboardMapper.getDashboardByUuid(paramDashboardVoUid);
        if (oldDashboardVo == null) {
            throw new DashboardNotFoundException(paramDashboardVoUid);
        }
        updateWidgetList(oldDashboardVo);
        //修改dashboard
        if (StringUtils.equals(oldDashboardVo.getType(), DashboardVo.DashBoardType.SYSTEM.getValue())) {
            if (AuthActionChecker.checkByUserUuid(UserContext.get().getUserUuid(), DASHBOARD_MODIFY.class.getSimpleName())) {
                //如果是复制的是系统类型的仪表板，且拥有仪表板的管理权限，复制仪表板的授权范围
                List<AuthorityVo> authorityList = dashboardMapper.getAuthorizedDashboardByDashboardUuid(paramDashboardVoUid).getAuthorityList();
                if (CollectionUtils.isNotEmpty(authorityList)) {
                    dashboardMapper.insertDashboardAuthorityList(authorityList, oldDashboardVo.getUuid());
                }
            } else {
                oldDashboardVo.setType(DashboardVo.DashBoardType.CUSTOM.getValue());
            }
        }
        oldDashboardVo.setName(paramDashboardVo.getName());
        dashboardMapper.insertDashboard(oldDashboardVo);

        return oldDashboardVo;

    }

    /**
     * 更新组件配置
     *
     * @param dashboardVo
     */
    private void updateWidgetList(DashboardVo dashboardVo) {
        List<DashboardWidgetVo> dashboardWidgetList = dashboardMapper.getDashboardWidgetByDashboardUuid(dashboardVo.getUuid());
        dashboardVo.setUuid(null);
        if (CollectionUtils.isNotEmpty(dashboardWidgetList)) {
            for (DashboardWidgetVo widgetVo : dashboardWidgetList) {
                if (StringUtils.isBlank(widgetVo.getChartConfig())) {
                    throw new DashboardParamException("widgetList.chartConfig");
                }
                widgetVo.setDashboardUuid(dashboardVo.getUuid());
                widgetVo.setUuid(null);
                dashboardMapper.insertDashboardWidget(widgetVo);
            }
        }
    }
}
