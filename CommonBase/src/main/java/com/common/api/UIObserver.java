package com.common.api;


import com.common.base.BaseView;

import androidx.lifecycle.Observer;

/**
 * 网络请求的UI处理观察者
 *
 * @author LiuFeng
 * @data 2020/4/28 17:55
 */
public abstract class UIObserver<T> implements Observer<NetResult<T>> {
    private BaseView view;

    public UIObserver(BaseView view) {
        this.view = view;
    }

    @Override
    public void onChanged(NetResult<T> tNetResult) {
        switch (tNetResult.status) {
            case Loading:
                showLoading();
                break;

            case Success:
                hideLoading();
                onSucceed(tNetResult.data);
                break;

            case Failed:
                hideLoading();
                onFailed(tNetResult.code, tNetResult.desc);
                break;

            case Complete:
                hideLoading();
                break;
        }
    }

    protected void showLoading() {
        if (view != null) {
            view.showLoading();
        }
    }

    protected void hideLoading() {
        if (view != null) {
            view.hideLoading();
        }
    }

    protected abstract void onSucceed(T result);

    protected void onFailed(int code, String desc) {
        if (view != null) {
            view.onError(code, desc);
        }
    }
}
