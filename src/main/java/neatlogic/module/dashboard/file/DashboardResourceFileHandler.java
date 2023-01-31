/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.dashboard.file;

import neatlogic.framework.file.core.FileTypeHandlerBase;
import neatlogic.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class DashboardResourceFileHandler extends FileTypeHandlerBase {

    @Override
    public boolean valid(String userUuid, FileVo fileVo, JSONObject jsonObj) {
        return true;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "仪表板自定义模板资源文件";
    }

    @Override
    protected boolean myDeleteFile(FileVo fileVo, JSONObject paramObj) {
        return false;
    }

    @Override
    public void afterUpload(FileVo fileVo, JSONObject jsonObj) {
    }

    @Override
    public String getName() {
        return "DASHBOARD";
    }

}
