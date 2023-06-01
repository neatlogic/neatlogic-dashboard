package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardWidgetNotFoundException extends ApiRuntimeException {
    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 4236727381596990432L;

    public DashboardWidgetNotFoundException(String uuid) {
        super("仪表板组件：{0}不存在", uuid);
    }
}
