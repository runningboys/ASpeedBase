package com.common.utils.dutil.callback;

public interface UploadCallback {
    /**
     * 开始
     */
    void onStart();

    /**
     * 上传中
     *
     * @param currentSize
     * @param totalSize
     * @param progress
     */
    void onProgress(long currentSize, long totalSize, float progress);

    /**
     * 完成
     *
     * @param response 响应data
     */
    void onFinish(String response);


    /**
     * 出错
     *
     * @param error
     */
    void onError(String error);
}
