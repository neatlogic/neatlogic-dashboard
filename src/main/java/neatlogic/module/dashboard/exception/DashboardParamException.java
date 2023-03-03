package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardParamException extends ApiRuntimeException {
    private static final long serialVersionUID = -8922008245435838956L;

    public DashboardParamException(String name) {
        super("exception.dashboard.dashboardparamexception", name);
    }
}