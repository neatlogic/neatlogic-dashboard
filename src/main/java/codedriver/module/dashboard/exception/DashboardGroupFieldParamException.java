package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardGroupFieldParamException extends ApiRuntimeException {

	private static final long serialVersionUID = -6341459237046543846L;

	public DashboardGroupFieldParamException() {
		super("'格式展示 -》分组条件'必填，请检查后重试");
	}
}
