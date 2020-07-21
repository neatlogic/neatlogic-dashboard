package codedriver.module.dashboard.api;

import codedriver.framework.reminder.core.OperationTypeEnum;
import codedriver.framework.restful.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.Chart;
import codedriver.framework.restful.core.ApiComponentBase;

@Service
@Transactional
@OperationType(type = OperationTypeEnum.SEARCH)
public class DashboardChartListApi extends ApiComponentBase {

	@Override
	public String getToken() {
		return "dashboard/chart/list";
	}

	@Override
	public String getName() {
		return "获取仪表板组件接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ 
		
	})
	@Output({ 
		@Param(name="",desc="")
	})
	@Description(desc = "修改默认仪表板接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		return Chart.getChartList();
	}
}
