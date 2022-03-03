/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dashboard.handler;

import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public interface IDashboardHandler {

	public String getType();

	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	/**
	 * @Time:Mar 2, 2020
	 * @Description: 获取唯一名
	 * @param @return
	 * @return String
	 */
	public String getName();
	
	/**
	* @Author: chenqiwei
	* @Time:Mar 20, 2020
	* @Description: 获取图标 
	* @param @return 
	* @return String
	 */
	public String getIcon();
	
	/**
	* @Author: chenqiwei
	* @Time:Mar 20, 2020
	* @Description: 获取显示名 
	* @param @return 
	* @return String
	 */
	public String getDisplayName();

	/**
	 * @Time:Mar 2, 2020
	 * @Description: 获取数据
	 * @param @return
	 * @return ChartDataVo
	 */
	public ChartDataVo getData(DashboardWidgetVo dashboardWidgetVo);

	/**
	 * @Time:Mar 2, 2020
	 * @Description: TODO 获取图表视图配置数据
	 * @param @return
	 * @return JSONObject
	 */
	public JSONObject getConfig(DashboardWidgetVo widgetVo);
	
}
