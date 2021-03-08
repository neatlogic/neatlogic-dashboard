package codedriver.framework.dashboard.core.charts;

import codedriver.framework.common.constvalue.dashboard.ChartType;
import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.dashboard.core.DashboardChartBase;
import codedriver.framework.dashboard.dto.DashboardShowConfigVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class NumberChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {ChartType.NUMBERCHART.getValue()};
	}

	@Override
	public JSONObject getData(JSONObject data) {
		JSONObject dataJson = new JSONObject();
		JSONArray dataList = new JSONArray();

		dataJson.put("dataList", dataList);
		return dataJson;
	}

	@Override
	public JSONObject getChartConfig() {
		JSONObject charConfig = new JSONObject();
		JSONObject showConfig = new JSONObject();
		showConfig.put(DashboardShowConfig.TYPE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.TYPE,JSONArray.parseArray("[{'value':'single','text':'单值','isDefault':1},{'value':'many','text':'多值'}]")));
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.AGGREGATE,JSONArray.parseArray("[{'value':'count','text':'计数','isDefault':1}]")));
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.GROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),new DashboardShowConfigVo(DashboardShowConfig.MAXGROUP,JSONArray.parseArray("[{'value':'10','text':'10','isDefault':1},{'value':'20','text':'20'}]")));
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),new DashboardShowConfigVo(DashboardShowConfig.REFRESHTIME,JSONArray.parseArray("[{'value':'-1','text':'不刷新','isDefault':1},{'value':'30','text':'30'}]")));
		showConfig.put(DashboardShowConfig.COLOR.getValue(),new DashboardShowConfigVo(DashboardShowConfig.COLOR,JSONArray.parseArray("[{'value':'#D18CBD','isDefault':1},{'value':'#FFBA5A'},{'value':'#78D8DE'},{'value':'#A78375'},{'value':'#B9D582'},{'value':'#898DDD'},{'value':'#F3E67B'},{'value':'#527CA6'},{'value':'#50BFF2'},{'value':'#FF6666'},{'value':'#15BF81'},{'value':'#90A4AE'}]")));
		charConfig.put("showConfig", showConfig);
		return charConfig;
	}
}
