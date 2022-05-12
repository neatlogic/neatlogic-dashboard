/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.dao.mapper;

import codedriver.framework.dashboard.dto.DashboardDefaultVo;
import codedriver.framework.dashboard.dto.DashboardVisitCounterVo;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.dto.AuthorityVo;
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
