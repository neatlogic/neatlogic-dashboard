package codedriver.framework.dashboard.core;

import codedriver.framework.dashboard.constvalue.IDashboardGroupField;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IDashboardChartCustom {

    /**
     * 获取支持的chart类型
     * @return chart类型
     */
    String[] getSupportChart();

    /**
     * 获取分组选项字段
     * @return 分组选项字段
     */
    List<IDashboardGroupField> getGroupFields();

    /**
     * 获取分组选项配置，用于前端渲染分组
     * @return 分组选项配置
     */
    JSONArray getGroupFieldsConfig();

    /**
     * 获取二级分组选项字段
     * @return 二级分组选项字段
     */
    List<IDashboardGroupField> getSubGroupFields();

    /**
     * 获取二级分组选项配置，用于前端渲染二级分组
     * @return 二级分组选项配置
     */
    JSONArray getSubGroupFieldsConfig();

    /**
     * 获取最终前端渲染配置
     * @param showConfig 默认前端渲染配置
     * @return 最终前端渲染配置
     */
    JSONArray getShowConfig(JSONObject showConfig);

    /**
     * 获取模块
     * @return 模块
     */
    String getModule();
}
