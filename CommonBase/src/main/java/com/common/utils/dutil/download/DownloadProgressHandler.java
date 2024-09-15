package com.common.utils.dutil.download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.common.utils.dutil.Utils.Utils;
import com.common.utils.dutil.data.Consts;
import com.common.utils.thread.UIHandler;

import java.io.File;

public class DownloadProgressHandler {
    private String url;
    private String path;
    private String name;
    private int    childTaskCount;

    private DownloadListener downloadCallback;
    private DownloadData     downloadData;

    private FileTask fileTask;

    private int mCurrentState = Consts.NONE;

    //是否支持断点续传
    private boolean isSupportRange;

    //重新开始下载需要先进行取消操作
    private boolean isNeedRestart;

    //记录已经下载的大小
    private int currentLength      = 0;
    //记录文件总大小
    private int totalLength        = 0;
    //记录已经暂停或取消的线程数
    private int tempChildTaskCount = 0;

    private long lastProgressTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            int mLastSate = mCurrentState;
            mCurrentState = msg.what;
            downloadData.setStatus(mCurrentState);

            switch (mCurrentState) {
                case Consts.START:
                    Bundle bundle = msg.getData();
                    totalLength = bundle.getInt("totalLength");
                    currentLength = bundle.getInt("currentLength");
                    String lastModify = bundle.getString("lastModify");
                    isSupportRange = bundle.getBoolean("isSupportRange");

                    if (!isSupportRange) {
                        childTaskCount = 1;
                    }
                    else if (currentLength == 0) {
                        DownloadData data = new DownloadData(url, path, childTaskCount, name, currentLength, totalLength, lastModify, System.currentTimeMillis());
                        // 存入数据库
                    }
                    if (downloadCallback != null) {
                        UIHandler.run(new Runnable() {
                            @Override
                            public void run() {
                                downloadCallback.onStart(currentLength, totalLength, Utils.getPercentage(currentLength, totalLength));
                            }
                        });
                    }
                    break;
                case Consts.PROGRESS:
                    synchronized (this) {
                        currentLength += msg.arg1;

                        downloadData.setPercentage(Utils.getPercentage(currentLength, totalLength));

                        if (downloadCallback != null && (System.currentTimeMillis() - lastProgressTime >= 20 || currentLength == totalLength)) {
                            UIHandler.run(new Runnable() {
                                @Override
                                public void run() {
                                    downloadCallback.onProgress(currentLength, totalLength, Utils.getPercentage(currentLength, totalLength));
                                }
                            });
                            lastProgressTime = System.currentTimeMillis();
                        }

                        if (currentLength == totalLength) {
                            sendEmptyMessage(Consts.FINISH);
                        }
                    }
                    break;
                case Consts.CANCEL:
                    synchronized (this) {
                        tempChildTaskCount++;
                        if (tempChildTaskCount == childTaskCount || mLastSate == Consts.PAUSE || mLastSate == Consts.ERROR) {
                            tempChildTaskCount = 0;
                            if (downloadCallback != null) {
                                UIHandler.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadCallback.onProgress(0, totalLength, 0);
                                    }
                                });
                            }
                            currentLength = 0;
                            if (isSupportRange) {
                                //Db.getInstance(context).deleteData(url);
                                Utils.deleteFile(new File(path, name + ".temp"));
                            }
                            Utils.deleteFile(new File(path, name));
                            if (downloadCallback != null) {
                                UIHandler.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadCallback.onCancel();
                                    }
                                });
                            }

                            if (isNeedRestart) {
                                isNeedRestart = false;
                                DownloadManger.getInstance().innerRestart(url);
                            }
                        }
                    }
                    break;
                case Consts.PAUSE:
                    synchronized (this) {
                        if (isSupportRange) {
                            //Db.getInstance(context).updateProgress(currentLength, Utils.getPercentage(currentLength, totalLength), Consts.PAUSE, url);
                        }
                        tempChildTaskCount++;
                        if (tempChildTaskCount == childTaskCount) {
                            if (downloadCallback != null) {
                                UIHandler.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadCallback.onPause(currentLength, totalLength, Utils.getPercentage(currentLength, totalLength));
                                    }
                                });
                            }
                            tempChildTaskCount = 0;
                        }
                    }
                    break;
                case Consts.FINISH:
                    if (isSupportRange) {
                        Utils.deleteFile(new File(path, name + ".temp"));
                        //Db.getInstance(context).deleteData(url);
                    }
                    if (downloadCallback != null) {
                        UIHandler.run(new Runnable() {
                            @Override
                            public void run() {
                                downloadCallback.onFinish(new File(path, name));
                            }
                        });
                    }
                    break;
                case Consts.DESTROY:
                    synchronized (this) {
                        if (isSupportRange) {
                            //Db.getInstance(context).updateProgress(currentLength, Utils.getPercentage(currentLength, totalLength), Consts.DESTROY, url);
                        }
                    }
                    break;
                case Consts.ERROR:
                    if (isSupportRange) {
                        //Db.getInstance(context).updateProgress(currentLength, Utils.getPercentage(currentLength, totalLength), Consts.ERROR, url);
                    }
                    if (downloadCallback != null) {
                        UIHandler.run(new Runnable() {
                            @Override
                            public void run() {
                                downloadCallback.onError((String) msg.obj);
                            }
                        });
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public DownloadProgressHandler(DownloadData downloadData, DownloadListener downloadCallback) {
        this.downloadCallback = downloadCallback;

        this.url = downloadData.getUrl();
        this.path = downloadData.getPath();
        this.name = downloadData.getName();
        this.childTaskCount = downloadData.getChildTaskCount();

        DownloadData dbData = DownloadManger.getInstance().getDbData(url);
        this.downloadData = dbData == null ? downloadData : dbData;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    public DownloadData getDownloadData() {
        return downloadData;
    }

    public void setFileTask(FileTask fileTask) {
        this.fileTask = fileTask;
    }

    /**
     * 下载中退出时保存数据、释放资源
     */
    public void destroy() {
        if (mCurrentState == Consts.CANCEL || mCurrentState == Consts.PAUSE) {
            return;
        }
        fileTask.destroy();
    }

    /**
     * 暂停（正在下载才可以暂停）
     * 如果文件不支持断点续传则不能进行暂停操作
     */
    public void pause() {
        if (mCurrentState == Consts.PROGRESS) {
            fileTask.pause();
        }
    }

    /**
     * 取消（已经被取消、下载结束则不可取消）
     */
    public void cancel(boolean isNeedRestart) {
        this.isNeedRestart = isNeedRestart;
        if (mCurrentState == Consts.PROGRESS) {
            fileTask.cancel();
        }
        else if (mCurrentState == Consts.PAUSE || mCurrentState == Consts.ERROR) {
            mHandler.sendEmptyMessage(Consts.CANCEL);
        }
    }
}
