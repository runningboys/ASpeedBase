package com.common.utils.dutil.download;

import java.io.File;

public interface DownloadListener {
    /**
     * 开始
     */
    void onStart(long currentSize, long totalSize, float progress);

    /**
     * 下载中
     *
     * @param currentSize
     * @param totalSize
     * @param progress
     */
    void onProgress(long currentSize, long totalSize, float progress);

    /**
     * 暂停
     */
    void onPause(long currentSize, long totalSize, float progress);

    /**
     * 取消
     */
    void onCancel();

    /**
     * 完成
     *
     * @param file
     */
    void onFinish(File file);

    /**
     * 等待下载
     */
    void onWait();

    /**
     * 出错
     *
     * @param error
     */
    void onError(String error);

    /**
     * 准备开始下载
     */
    void onStartDownload(FileInfo file);

    /**
     * 下载进度
     *
     * @param processed
     * @param total
     */
    void onDownloading(FileInfo file, long processed, long total);

    /**
     * 暂停下载
     *
     * @param processed
     * @param total
     */
    void onDownloadPause(FileInfo file, long processed, long total);

    /**
     * 取消下载
     *
     * @param file
     */
    void onDownloadCancel(File file);

    /**
     * 下载成功后的文件
     *
     * @param file 下载成功后的文件
     */
    void onDownloadSuccess(File file);

    /**
     * 下载异常信息
     *
     * @param e 下载异常信息
     */
    void onDownloadFailed(Exception e);

    /**
     * 文件下载操作进度
     *
     * @param file
     * @param progress
     * @param total
     */
    public void onFileDownloading(FileInfo file, long progress, long total);

    /**
     * 文件下载完成
     *
     * @param file
     */
    public void onFileDownloadCompleted(FileInfo file);

    /**
     * 文件取消操作
     *
     * @param file
     */
    public void onFileCanceled(FileInfo file);

    /**
     * 文件操作错误
     *
     * @param file
     * @param error
     */
    public void onFileManagerFailed(FileInfo file, String error);
}
