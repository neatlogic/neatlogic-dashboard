package codedriver.module.dashboard.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardNotFoundException extends ApiRuntimeException {
	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = 2115999834233454277L;

	public DashboardNotFoundException(String uuid) {
		super("仪表板：" + uuid + "不存在");
	}

}
