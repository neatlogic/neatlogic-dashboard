package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4778633677540696671L;

    public DashboardHandlerNotFoundException(String handler) {
        super("exception.dashboard.dashboardhandlernotfoundexception", handler);
    }
}
