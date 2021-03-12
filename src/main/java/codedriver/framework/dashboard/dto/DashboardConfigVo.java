package codedriver.framework.dashboard.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedHashMap;

/**
 * @Title: DashboardConfigVo
 * @Package: codedriver.framework.dashboard.dto
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/3/12 12:06
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class DashboardConfigVo {
    private String group;
    private String subGroup;
    private LinkedHashMap<String,String> groupDataCountMap;
    private String groupType;
    private JSONObject chartConfig;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public LinkedHashMap<String, String> getGroupDataCountMap() {
        return groupDataCountMap;
    }

    public void setGroupDataCountMap(LinkedHashMap<String, String> groupDataCountMap) {
        this.groupDataCountMap = groupDataCountMap;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public JSONObject getChartConfig() {
        return chartConfig;
    }

    public void setChartConfig(JSONObject chartConfig) {
        this.chartConfig = chartConfig;
    }
}
