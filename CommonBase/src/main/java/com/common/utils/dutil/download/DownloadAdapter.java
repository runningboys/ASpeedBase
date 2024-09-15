package com.common.utils.dutil.download;

import java.io.File;

public abstract class DownloadAdapter implements DownloadListener {

    @Override
    public void onStart(long currentSize, long totalSize, float progress) {}

    @Override
    public void onProgress(long currentSize, long totalSize, float progress) {}

    @Override
    public void onPause(long currentSize, long totalSize, float progress) {}

    @Override
    public void onCancel() {}

    @Override
    public void onFinish(File file) {}

    @Override
    public void onWait() {}

    @Override
    public void onError(String error) {}
}
