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

package neatlogic.module.dashboard.dao.mapper;

import neatlogic.framework.dashboard.dto.DashboardDefaultVo;
import neatlogic.framework.dashboard.dto.DashboardVisitCounterVo;
import neatlogic.framework.dashboard.dto.DashboardVo;
import neatlogic.framework.dashboard.dto.DashboardWidgetVo;
import neatlogic.framework.dto.AuthorityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DashboardMapper {
    List<DashboardDefaultVo> getDefaultDashboardUuidByUserUuid(String userUuid);

    int searchDashboardCount(DashboardVo dashboardVo);

    List<Long> searchDashboardId(DashboardVo dashboardVo);

    List<String> searchAuthorizedDashboardUuid(DashboardVo dashboardVo);

    List<DashboardVo> getDashboardByIdList(@Param("idList") List<Long> idList);

    // List<DashboardVo> searchTopVisitDashboard(DashboardVo dashboardVo);

    int checkDashboardNameIsExists(DashboardVo dashboardVo);

    //DashboardWidgetVo getDashboardWidgetByUuid(String dashboardWidgetUuid);

    DashboardVo getDashboardById(Long id);

    DashboardVo getAuthorizedDashboardByDashboardUuid(String dashboardUuid);

    DashboardVo getAuthorizedDashboardByUuid(DashboardVo dashboardVo);

    DashboardVo getSystemDashBoardByName(String name);

    DashboardVisitCounterVo getDashboardVisitCounter(@Param("dashboardId") Long dashboardId, @Param("userUuid") String userUuid);

    //List<DashboardWidgetVo> getDashboardWidgetByDashboardUuid(String dashboardUuid);

    int updateDashboard(DashboardVo dashboardVo);

    int updateDashboardVisitCounter(DashboardVisitCounterVo dashboardVisitCounterVo);

    int insertDashboard(DashboardVo dashboardVo);

    int insertDashboardAuthority(@Param("authorityVo") AuthorityVo authorityVo, @Param("dashboardId") Long dashboardId);

    int insertDashboardAuthorityList(@Param("authorityList") List<AuthorityVo> authorityList, @Param("dashboardId") Long dashboardId);

    int insertDashboardWidget(DashboardWidgetVo dashboardWidgetVo);

    int insertDashboardDefault(@Param("dashboardUuid") String dashboardUuid, @Param("userUuid") String userUuid, @Param("type") String type);

    int insertDashboardVisitCounter(DashboardVisitCounterVo dashboardVisitCounterVo);

    int deleteDashboardById(Long dashboardId);

    int deleteDashboardWidgetByDashboardUuid(String dashboardUuid);

    int deleteDashboardAuthorityByDashboardId(@Param("dashboardId") Long dashboardId);

    int deleteDashboardDefaultByDashboardId(Long dashboardId);

    int deleteDashboardVisitCounterByDashboardId(Long dashboardId);

    int deleteDashboardDefaultByUserUuid(@Param("userUuid") String userUuid, @Param("type") String type);

    //int deleteDashboardWidgetByUuid(@Param("dashboardUuid") String dashboardUuid, @Param("uuid") String uuid);
}
