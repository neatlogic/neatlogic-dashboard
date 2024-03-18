/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
