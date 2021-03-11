package codedriver.framework.dashboard.dto;

/**
 * @Title: DashboardDataVo
 * @Package: codedriver.module.report.dto
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/3/9 17:47
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class DashboardDataVo {
    private DashboardDataGroupVo dataGroupVo;
    private DashboardDataSubGroupVo dataSubGroupVo;

    public DashboardDataGroupVo getDataGroupVo() {
        return dataGroupVo;
    }

    public void setDataGroupVo(DashboardDataGroupVo dataGroupVo) {
        this.dataGroupVo = dataGroupVo;
    }

    public DashboardDataSubGroupVo getDataSubGroupVo() {
        return dataSubGroupVo;
    }

    public void setDataSubGroupVo(DashboardDataSubGroupVo dataSubGroupVo) {
        this.dataSubGroupVo = dataSubGroupVo;
    }
}
