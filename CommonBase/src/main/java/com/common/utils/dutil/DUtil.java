package com.common.utils.dutil;

import com.common.utils.dutil.upload.DirectUploadBuilder;
import com.common.utils.dutil.upload.FormUploadBuilder;

public class DUtil {

    /**
     * 表单式文件上传
     *
     * @return
     */
    public static FormUploadBuilder uploadToForm() {
        return new FormUploadBuilder();
    }

    /**
     * 将文件作为请求体上传
     *
     * @return
     */
    public static DirectUploadBuilder uploadToDirect() {
        return new DirectUploadBuilder();
    }
}
