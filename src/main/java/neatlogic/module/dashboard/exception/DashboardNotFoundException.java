/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DashboardNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = 2115999834233454277L;

    public DashboardNotFoundException(Long id) {
        super("仪表板“" + id + "”不存在");
    }

}
