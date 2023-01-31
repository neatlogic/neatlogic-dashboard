/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.auth.label;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.label.DATA_WAREHOUSE_BASE;

import java.util.Collections;
import java.util.List;

public class DASHBOARD_BASE extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "面板查看权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "查看系统类面板";
    }

    @Override
    public String getAuthGroup() {
        return "dashboard";
    }

    @Override
    public Integer getSort() {
        return 1;
    }

    public List<Class<? extends AuthBase>> getIncludeAuths() {
        return Collections.singletonList(DATA_WAREHOUSE_BASE.class);
    }

}
