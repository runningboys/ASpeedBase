package com.common.utils.dutil.download;

import com.common.utils.dutil.data.Consts;
import com.common.utils.dutil.net.OkHttpManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadManger {

    private Map<String, DownloadProgressHandler> progressHandlerMap = new HashMap<>();//保存任务的进度处理对象
    private Map<String, DownloadData>            downloadDataMap    = new HashMap<>();//保存任务数据
    private Map<String, DownloadListener>        callbackMap        = new HashMap<>();//保存任务回调
    private Map<String, FileTask>                fileTaskMap        = new HashMap<>();//保存下载线程

    private static DownloadManger instance = new DownloadManger();

    private DownloadManger() {}

    public static DownloadManger getInstance() {
        return instance;
    }

    /**
     * 配置线程池
     *
     * @param corePoolSize
     * @param maxPoolSize
     */
    public void setTaskPoolSize(int corePoolSize, int maxPoolSize) {
        if (maxPoolSize > corePoolSize && maxPoolSize * corePoolSize != 0) {
            ThreadPool.getInstance().setCorePoolSize(corePoolSize);
            ThreadPool.getInstance().setMaxPoolSize(maxPoolSize);
        }
    }

    /**
     * data + callback 形式直接开始下载
     *
     * @param downloadData
     * @param listener
     *
     * @return
     */
    public void start(DownloadData downloadData, DownloadListener listener) {
        execute(downloadData, listener);
    }

    /**
     * 根据url开始下载（需先注册监听）
     *
     * @param url
     */
    public void start(String url) {
        execute(downloadDataMap.get(url), callbackMap.get(url));
    }

    /**
     * 注册监听
     *
     * @param downloadData
     * @param downloadCallback
     */
    public synchronized void addDownload(DownloadData downloadData, DownloadListener downloadCallback) {
        downloadDataMap.put(downloadData.getUrl(), downloadData);
        callbackMap.put(downloadData.getUrl(), downloadCallback);
    }

    /**
     * 配置https证书
     *
     * @param certificates
     */
    public void setCertificates(InputStream... certificates) {
        OkHttpManager.getInstance().setCertificates(certificates);
    }

    /**
     * 获得数据库中对应url下载数据
     *
     * @param url
     *
     * @return
     */
    public DownloadData getDbData(String url) {
        return null;
    }

    /**
     * 获得数据库中所有下载数据
     *
     * @return
     */
    public List<DownloadData> getAllDbData() {
        return null;
    }

    /**
     * 根据url获得下载队列中的data
     *
     * @param url
     *
     * @return
     */
    public DownloadData getCurrentData(String url) {
        if (progressHandlerMap.containsKey(url)) {
            return progressHandlerMap.get(url).getDownloadData();
        }
        return null;
    }

    /**
     * 执行下载任务
     */
    private synchronized void execute(DownloadData downloadData, DownloadListener downloadCallback) {
        //防止同一个任务多次下载
        if (progressHandlerMap.get(downloadData.getUrl()) != null) {
            return;
        }

        //默认每个任务不通过多个异步任务下载
        if (downloadData.getChildTaskCount() == 0) {
            downloadData.setChildTaskCount(1);
        }

        DownloadProgressHandler downloadProgressHandler = new DownloadProgressHandler(downloadData, downloadCallback);
        FileTask fileTask = new FileTask(downloadData, downloadProgressHandler.getHandler());
        downloadProgressHandler.setFileTask(fileTask);

        downloadDataMap.put(downloadData.getUrl(), downloadData);
        callbackMap.put(downloadData.getUrl(), downloadCallback);
        fileTaskMap.put(downloadData.getUrl(), fileTask);
        progressHandlerMap.put(downloadData.getUrl(), downloadProgressHandler);

        ThreadPool.getInstance().getThreadPoolExecutor().execute(fileTask);

        //如果正在下载的任务数量等于线程池的核心线程数，则新添加的任务处于等待状态
        if (ThreadPool.getInstance().getThreadPoolExecutor().getActiveCount() == ThreadPool.getInstance().getCorePoolSize()) {
            downloadCallback.onWait();
        }
    }

    /**
     * 暂停
     *
     * @param url
     */
    public void pause(String url) {
        if (progressHandlerMap.containsKey(url)) {
            progressHandlerMap.get(url).pause();
        }
    }

    /**
     * 继续
     *
     * @param url
     */
    public void resume(String url) {
        if (progressHandlerMap.containsKey(url) && (progressHandlerMap.get(url).getCurrentState() == Consts.PAUSE || progressHandlerMap.get(url).getCurrentState() == Consts.ERROR)) {
            progressHandlerMap.remove(url);
            execute(downloadDataMap.get(url), callbackMap.get(url));
        }
    }

    /**
     * 重新开始
     *
     * @param url
     */
    public void restart(String url) {
        //文件已下载完成的情况
        if (progressHandlerMap.containsKey(url) && progressHandlerMap.get(url).getCurrentState() == Consts.FINISH) {
            progressHandlerMap.remove(url);
            fileTaskMap.remove(url);
            innerRestart(url);
            return;
        }

        //任务已经取消，则直接重新下载
        if (!progressHandlerMap.containsKey(url)) {
            innerRestart(url);
        }
        else {
            innerCancel(url, true);
        }
    }

    /**
     * 实际的重新下载操作
     *
     * @param url
     */
    protected void innerRestart(String url) {
        execute(downloadDataMap.get(url), callbackMap.get(url));
    }

    /**
     * 取消
     *
     * @param url
     */
    public void cancel(String url) {
        innerCancel(url, false);
    }

    public void innerCancel(String url, boolean isNeedRestart) {
        if (progressHandlerMap.get(url) != null) {
            if (progressHandlerMap.get(url).getCurrentState() == Consts.NONE) {
                //取消缓存队列中等待下载的任务
                ThreadPool.getInstance().getThreadPoolExecutor().remove(fileTaskMap.get(url));
                callbackMap.get(url).onCancel();
            }
            else {
                //取消已经开始下载的任务
                progressHandlerMap.get(url).cancel(isNeedRestart);
            }
            progressHandlerMap.remove(url);
            fileTaskMap.remove(url);
        }
    }

    /**
     * 退出时释放资源
     *
     * @param url
     */
    public void destroy(String url) {
        if (progressHandlerMap.containsKey(url)) {
            progressHandlerMap.get(url).destroy();
            progressHandlerMap.remove(url);
            callbackMap.remove(url);
            downloadDataMap.remove(url);
            fileTaskMap.remove(url);
        }
    }

    public void destroy(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                destroy(url);
            }
        }
    }
}
