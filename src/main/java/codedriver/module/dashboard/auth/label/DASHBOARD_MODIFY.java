package codedriver.module.dashboard.auth.label;

import codedriver.framework.auth.core.AuthBase;

import java.util.Collections;
import java.util.List;

public class DASHBOARD_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "面板管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对系统类面板进行新增、编辑、授权和删除";
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
