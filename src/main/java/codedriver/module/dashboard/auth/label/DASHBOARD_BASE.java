package codedriver.module.dashboard.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class DASHBOARD_BASE extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "面板查看权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "查看系统类面板";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}

	@Override
	public Integer sort() {
		return 1;
	}
}
