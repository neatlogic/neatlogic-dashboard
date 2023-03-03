package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardFieldNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 809005908371594393L;

    public DashboardFieldNotFoundException(String field) {
        super("exception.dashboard.dashboardfieldnotfoundexception", field);
    }
}
