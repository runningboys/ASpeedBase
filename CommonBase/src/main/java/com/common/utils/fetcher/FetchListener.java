package com.common.utils.fetcher;

/**
 * 截取操作处理监听。
 */
public interface FetchListener {

    /**
     * 当截取操作完成后调用该方法。
     *
     * @param result
     */
    public void onCompleted(FetcherResult result);

    /**
     * 当截取操作失败时调用该方法。
     *
     * @param error
     */
    public void onFailed(FetcherError error);

}
