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

package neatlogic.module.dashboard.api;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.util.TableResultUtil;
import neatlogic.module.dashboard.auth.label.DASHBOARD_BASE;
import neatlogic.module.dashboard.dao.mapper.DashboardMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class SearchDashboardApi extends PrivateApiComponentBase {

    @Resource
    private DashboardMapper dashboardMapper;

    @Override
    public String getToken() {
        return "dashboard/search";
    }

    @Override
    public String getName() {
        return "nmda.searchdashboardapi.getname";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "keyword", type = ApiParamType.STRING, desc = "common.keyword"),
            @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "common.isactive"),
            @Param(name = "searchType", type = ApiParamType.ENUM, rule = "all,system,custom", desc = "common.type", help = "all：所有，system：系统面板，custom：个人面板，默认值：all"),
            @Param(name = "currentPage", type = ApiParamType.INTEGER, desc = "common.currentpage"),
            @Param(name = "pageSize", type = ApiParamType.INTEGER, desc = "common.pagesize"),
            @Param(name = "needPage", type = ApiParamType.BOOLEAN, desc = "common.isneedpage")})
    @Output({
            @Param(explode = BasePageVo.class),
            @Param(name = "tbodyList", explode = DashboardVo[].class, desc = "common.tbodylist")
    })
    @Description(desc = "nmda.searchdashboardapi.getname")
    @Override
    public Object myDoService(JSONObject jsonObj) {
        DashboardVo dashboardVo = JSONObject.toJavaObject(jsonObj, DashboardVo.class);
        String userUuid = UserContext.get().getUserUuid(true);
        dashboardVo.setFcu(userUuid);
        if (!dashboardVo.getIsAdmin()) {
            AuthenticationInfoVo authenticationInfoVo = UserContext.get().getAuthenticationInfoVo();
            dashboardVo.setUserUuid(authenticationInfoVo.getUserUuid());
            dashboardVo.setTeamUuidList(authenticationInfoVo.getTeamUuidList());
            dashboardVo.setRoleUuidList(authenticationInfoVo.getRoleUuidList());
        }
        int rowNum = dashboardMapper.searchDashboardCount(dashboardVo);
        dashboardVo.setRowNum(rowNum);
        List<Long> dashboardIdList = dashboardMapper.searchDashboardId(dashboardVo);

        List<DashboardVo> dashboardList = null;
        if (CollectionUtils.isNotEmpty(dashboardIdList)) {
            dashboardList = dashboardMapper.getDashboardByIdList(dashboardIdList);
        }

        return TableResultUtil.getResult(dashboardList, dashboardVo);
    }
}
