/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.dashboard.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dto.AuthenticationInfoVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.OperationType;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateBinaryStreamApiComponentBase;
import codedriver.framework.service.AuthenticationInfoService;
import codedriver.framework.util.FileUtil;
import codedriver.module.dashboard.auth.label.DASHBOARD_BASE;
import codedriver.module.dashboard.dao.mapper.DashboardMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AuthAction(action = DASHBOARD_BASE.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class ExportDashboardApi extends PrivateBinaryStreamApiComponentBase {

    @Resource
    private DashboardMapper dashboardMapper;

    @Resource
    private AuthenticationInfoService authenticationInfoService;

    @Override
    public String getToken() {
        return "dashboard/export";
    }

    @Override
    public String getName() {
        return "导出仪表板";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "keyword", type = ApiParamType.STRING, desc = "关键字"),
            @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "是否激活"),
            @Param(name = "searchType", type = ApiParamType.ENUM, rule = "all,system,custom", desc = "类型，all或mine，默认值:all"),
    })
    @Description(desc = "导出仪表板")
    @Override
    public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DashboardVo dashboardVo = JSONObject.toJavaObject(paramObj, DashboardVo.class);
        String userUuid = UserContext.get().getUserUuid(true);
        dashboardVo.setFcu(userUuid);
        if (!dashboardVo.getIsAdmin()) {
            AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userUuid);
            dashboardVo.setUserUuid(authenticationInfoVo.getUserUuid());
            dashboardVo.setTeamUuidList(authenticationInfoVo.getTeamUuidList());
            dashboardVo.setRoleUuidList(authenticationInfoVo.getRoleUuidList());
        }
        int rowNum = dashboardMapper.searchDashboardCount(dashboardVo);
        dashboardVo.setRowNum(rowNum);
        if (rowNum > 0) {
            String fileName = FileUtil.getEncodedFileName("仪表板." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pak");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", " attachment; filename=\"" + fileName + "\"");
            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                for (int i = 1; i <= dashboardVo.getPageCount(); i++) {
                    dashboardVo.setCurrentPage(i);
                    List<Long> dashboardIdList = dashboardMapper.searchDashboardId(dashboardVo);
                    if (!dashboardIdList.isEmpty()) {
                        List<DashboardVo> dashboardList = dashboardMapper.getDashboardByIdList(dashboardIdList);
                        if (!dashboardList.isEmpty()) {
                            for (DashboardVo vo : dashboardList) {
                                zos.putNextEntry(new ZipEntry(vo.getName() + ".json"));
                                zos.write(JSONObject.toJSONBytes(vo));
                                zos.closeEntry();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
