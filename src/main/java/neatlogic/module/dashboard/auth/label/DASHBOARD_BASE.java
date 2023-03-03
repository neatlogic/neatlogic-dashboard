/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.dashboard.auth.label;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.label.DATA_WAREHOUSE_BASE;

import java.util.Collections;
import java.util.List;

public class DASHBOARD_BASE extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "auth.dashboard.dashboardbase.name";
    }

    @Override
    public String getAuthIntroduction() {
        return "auth.dashboard.dashboardbase.introduction";
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
