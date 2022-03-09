package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardStatisticsParamException extends ApiRuntimeException {

	private static final long serialVersionUID = -2962374819787746397L;

	public DashboardStatisticsParamException() {
		super("'格式展示 -》聚合方式'必填，请检查后重试");
	}
}
