package codedriver.framework.dashboard.dto;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class ChartDataVo {
	@EntityField(name = "值字段", type = ApiParamType.STRING)
	private String valueField;
	@EntityField(name = "值字段名称", type = ApiParamType.STRING)
	private String valueFieldText;
	@EntityField(name = "二级分组字段", type = ApiParamType.STRING)
	private String subGroupField;
	@EntityField(name = "二级分组字段名称", type = ApiParamType.STRING)
	private String subGroupFieldText;
	@EntityField(name = "分组字段", type = ApiParamType.STRING)
	private String groupField;
	@EntityField(name = "分组字段名称", type = ApiParamType.STRING)
	private String groupFieldText;
	@EntityField(name = "数据集", type = ApiParamType.JSONARRAY)
	private JSONObject data;
	@EntityField(name = "图表配置", type = ApiParamType.JSONOBJECT)
	private JSONObject configObj;

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public JSONObject getConfigObj() {
		return configObj;
	}

	public void setConfigObj(JSONObject configObj) {
		this.configObj = configObj;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getGroupField() {
		return groupField;
	}

	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}

	public String getSubGroupField() {
		return subGroupField;
	}

	public void setSubGroupField(String subGroupField) {
		this.subGroupField = subGroupField;
	}

	public String getValueFieldText() {
		return valueFieldText;
	}

	public void setValueFieldText(String valueFieldText) {
		this.valueFieldText = valueFieldText;
	}

	public String getSubGroupFieldText() {
		return subGroupFieldText;
	}

	public void setSubGroupFieldText(String subGroupFieldText) {
		this.subGroupFieldText = subGroupFieldText;
	}

	public String getGroupFieldText() {
		return groupFieldText;
	}

	public void setGroupFieldText(String groupFieldText) {
		this.groupFieldText = groupFieldText;
	}
	
}
