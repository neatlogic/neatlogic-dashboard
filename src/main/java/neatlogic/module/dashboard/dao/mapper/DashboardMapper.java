/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
