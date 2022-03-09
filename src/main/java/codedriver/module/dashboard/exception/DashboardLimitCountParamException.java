package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardLimitCountParamException extends ApiRuntimeException {

	private static final long serialVersionUID = 1303238213985736668L;

	public DashboardLimitCountParamException() {
		super("'格式展示 -》最大组数量'必填，请检查后重试");
	}
}
