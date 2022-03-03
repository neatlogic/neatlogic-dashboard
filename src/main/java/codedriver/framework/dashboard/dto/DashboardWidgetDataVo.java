/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dashboard.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class DashboardWidgetDataVo {
    @EntityField(name = "总数", type = ApiParamType.STRING)
    private String total;

    @EntityField(name = "分组值", type = ApiParamType.STRING)
    private String value;

    @EntityField(name = "分组名", type = ApiParamType.STRING)
    private String column;

    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
