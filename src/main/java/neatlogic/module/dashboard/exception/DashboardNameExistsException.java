package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardNameExistsException extends ApiRuntimeException {
    private static final long serialVersionUID = 4230545560652554738L;

    public DashboardNameExistsException(String uuid) {
        super("仪表板：“{0}”已存在", uuid);
    }

}
