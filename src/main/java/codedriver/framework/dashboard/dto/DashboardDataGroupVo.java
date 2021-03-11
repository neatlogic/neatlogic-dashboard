package codedriver.framework.dashboard.dto;

import java.util.List;
import java.util.Map;

/**
 * @Title: DashboardDataGroupVo
 * @Package: codedriver.module.report.dto
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/3/9 17:48
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class DashboardDataGroupVo {
    private String primaryKey;
    private String handleName;
    private String proName;
    private List<Map<String, String>> dataList;
    private Map<String, String> dataCountMap;

    public DashboardDataGroupVo() {
    }

    public DashboardDataGroupVo(String primaryKey, String handleName, String proName, Map<String, String> dataCountMap) {
        this.primaryKey = primaryKey;
        this.handleName = handleName;
        this.proName = proName;
        this.dataCountMap = dataCountMap;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public Map<String, String> getDataCountMap() {
        return dataCountMap;
    }

    public void setDataCountMap(Map<String, String> dataCountMap) {
        this.dataCountMap = dataCountMap;
    }
}
