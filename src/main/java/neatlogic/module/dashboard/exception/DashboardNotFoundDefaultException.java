package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardNotFoundDefaultException extends ApiRuntimeException {

    private static final long serialVersionUID = 339485229374813366L;

    public DashboardNotFoundDefaultException() {
        super("exception.dashboard.dashboardnotfounddefaultexception");
    }

}
