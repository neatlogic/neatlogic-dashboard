/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardGroupFieldParamException extends ApiRuntimeException {

	private static final long serialVersionUID = -6341459237046543846L;

	public DashboardGroupFieldParamException() {
		super("格式展示 -> 分组条件 不能为空，请检查后重试");
	}
}
