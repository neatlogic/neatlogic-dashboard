package codedriver.module.dashboard.api;

import java.util.List;

import codedriver.framework.reminder.core.OperationTypeEnum;
import codedriver.framework.restful.annotation.OperationType;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardHandlerFactory;
import codedriver.framework.dashboard.dto.DashboardHandlerVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;

@OperationType(type = OperationTypeEnum.SEARCH)
@Service
public class DashboardHandlerListApi extends ApiComponentBase {

	@Override
	public String getToken() {
		return "dashboard/handler/list";
	}

	@Override
	public String getName() {
		return "仪表板数据源列表接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Output({ @Param(name = "Type", explode = DashboardHandlerVo[].class, desc = "仪表板组件信息，key：分类，value：组件列表") })
	@Description(desc = "仪表板数据源列表接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		List<DashboardHandlerVo> dashboardHandlerList = DashboardHandlerFactory.getDashboardHandlerList();
		JSONObject returnObj = new JSONObject();
		for (DashboardHandlerVo handlerVo : dashboardHandlerList) {
			if (returnObj.containsKey(handlerVo.getType())) {
				returnObj.getJSONArray(handlerVo.getType()).add(handlerVo);
			} else {
				JSONArray objList = new JSONArray();
				objList.add(handlerVo);
				returnObj.put(handlerVo.getType(), objList);
			}
		}
		return returnObj;
	}
}
