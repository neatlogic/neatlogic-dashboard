package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class DashboardNameExistsException extends ApiFieldValidRuntimeException {
	private static final long serialVersionUID = 4230545560652554738L;

	public DashboardNameExistsException(String uuid) {
		super("仪表板：" + uuid + "已存在");
	}

}
