/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardAuthenticationException extends ApiRuntimeException {
    private static final long serialVersionUID = 1997483988896454848L;

    public DashboardAuthenticationException(String action) {
        super("您没有" + action + "当前仪表板的权限");
    }

}
