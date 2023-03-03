package neatlogic.module.dashboard.auth.label;

import neatlogic.framework.auth.core.AuthBase;

import java.util.Collections;
import java.util.List;

public class DASHBOARD_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "auth.dashboard.dashboardmodify.name";
	}

	@Override
	public String getAuthIntroduction() {
		return "auth.dashboard.dashboardmodify.introduction";
	}

	@Override
	public String getAuthGroup() {
		return "dashboard";
	}

	@Override
	public Integer getSort() {
		return 2;
	}

	@Override
	public List<Class<? extends AuthBase>> getIncludeAuths(){
		return Collections.singletonList(DASHBOARD_BASE.class);
	}
}
