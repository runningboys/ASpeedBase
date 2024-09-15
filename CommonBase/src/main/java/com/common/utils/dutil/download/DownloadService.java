package com.common.utils.dutil.download;

/**
 * 下载服务
 *
 * @author LiuFeng
 * @data 2019/1/31 18:04
 */
public interface DownloadService {

    /**
     * 添加下载监听
     *
     * @param listener
     */
    void addDownloadListener(DownloadListener listener);

    /**
     * 删除下载监听
     *
     * @param listener
     */
    void removeDownloadListener(DownloadListener listener);

    /**
     * 下载文件
     *
     * @param fileInfo
     */
    void download(FileInfo fileInfo);
    /**
     * 下载
     *
     * @param fileInfo
     * @param listener
     */
    void download(FileInfo fileInfo, DownloadListener listener);

    /**
     * 暂停下载文件
     *
     * @param fileInfo
     */
    void pauseDownload(FileInfo fileInfo);

    /**
     * 恢复下载文件
     *
     * @param fileInfo
     */
    void resumeDownload(FileInfo fileInfo);

    /**
     * 取消下载
     *
     * @param fileInfo
     */
    void cancelDownload(FileInfo fileInfo);
}
