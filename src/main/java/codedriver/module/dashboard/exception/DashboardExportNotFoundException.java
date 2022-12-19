/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardExportNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 2953134440892832028L;

    public DashboardExportNotFoundException() {
        super("暂无可导出的仪表板");
    }

}
