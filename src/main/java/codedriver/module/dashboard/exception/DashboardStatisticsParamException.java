/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardStatisticsParamException extends ApiRuntimeException {

	private static final long serialVersionUID = -2962374819787746397L;

	public DashboardStatisticsParamException() {
		super("格式展示 -> 聚合方式 不能为空，请检查后重试");
	}
}
