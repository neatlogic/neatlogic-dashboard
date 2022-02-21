package codedriver.framework.dashboard.core;

import codedriver.framework.dashboard.dto.DashboardDataGroupVo;
import codedriver.framework.dashboard.dto.DashboardDataSubGroupVo;
import codedriver.framework.dashboard.dto.DashboardWidgetDataVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public abstract class DashboardChartBase {
    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:Mar 20, 2020
     * @Description: 要和dashboard_widget表chart_type字段枚举值一致
     */
    public abstract String[] getSupportChart();

    /**
     * @param @return
     * @return JSONObject
     * @Description: 返回数据
     */
    public JSONObject getData(DashboardWidgetDataVo dashboardDataVo) {
        return getMyData(dashboardDataVo);
    }

    public JSONObject getMyData(DashboardWidgetDataVo dashboardDataVo) {
        JSONObject dataJson = new JSONObject();
        dataJson.put("dataList", getDefaultData(dashboardDataVo));
        return dataJson;
    }

    /**
     * @Description: 支持 普遍chart的数据处理
     * @Author: 89770
     * @Date: 2021/3/12 15:01
     * @Params: [dashboardDataVo]
     * @Returns: com.alibaba.fastjson.JSONObject
     **/
    protected List<Map<String, Object>> getDefaultData(DashboardWidgetDataVo dashboardDataVo) {
        List<Map<String, Object>> resultDataList = new ArrayList<>();
        DashboardDataGroupVo dataGroupVo = dashboardDataVo.getDataGroupVo();
        DashboardDataSubGroupVo dataSubGroupVo = dashboardDataVo.getDataSubGroupVo();
        if (CollectionUtils.isNotEmpty(dataGroupVo.getDataList())) {
            Map<String, Object> groupDataCountMap = dataGroupVo.getDataCountMap();
            //循环获取需要的字段数据
            for (Map<String, Object> dataMap : dataGroupVo.getDataList()) {
                Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();
                Map<String, Object> resultDataMap = new HashMap<>();
                //如果不包含primaryKey 或 存在值为null 的列，则废弃该数据
                if (!dataMap.containsKey(dataGroupVo.getPrimaryKey()) || dataMap.containsValue(null)) {
                    continue;
                }
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    //如果是分组
                    if (dataGroupVo.getPrimaryKey().equals(key)) {
                        if (dataSubGroupVo != null) {
                            resultDataMap.put("total", groupDataCountMap.get(value));
                        } else {
                            resultDataMap.put("total", dataMap.get("count"));
                        }
                    }
                    if (dataGroupVo.getProName().equals(key)) {
                        resultDataMap.put("column", value);
                    }
                    //如果是子分组
                    if (dataSubGroupVo != null && dataSubGroupVo.getProName().equals(key)) {
                        resultDataMap.put("type", value);
                    }

                    if ("count".equals(key)) {
                        resultDataMap.put("value", dataMap.get("count"));
                    }
                }
                resultDataList.add(resultDataMap);
            }
        }
        return resultDataList;
    }

    /**
     * @param @return
     * @return JSONObject
     * @Description: 回显规则
     */
    public abstract JSONObject getChartConfig();


}
