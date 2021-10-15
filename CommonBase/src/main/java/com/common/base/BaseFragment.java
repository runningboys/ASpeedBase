package com.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.common.base.ability.IBaseView;
import com.common.base.ability.IEventBus;
import com.common.base.ability.INetMonitor;
import com.common.eventbus.Event;
import com.common.utils.ClickUtil;
import com.common.utils.log.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment基类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public abstract class BaseFragment extends Fragment implements IEventBus, INetMonitor, IBaseView, View.OnClickListener {

    protected View mRootView;
    protected Activity mActivity;

    //fragment布局资源id
    private int containerId;

    // 标识fragment视图已经初始化完毕
    private boolean mIsViewPrepared;

    // 标识已经触发过懒加载数据
    private boolean mHasLoadData;

    private boolean isDestroyed;

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    /**
     * 获取布局文件id
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 初始化组件
     */
    protected void initView() {
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.i("onCreate");
        super.onCreate(savedInstanceState);
        createEventBus();
        createNetMonitor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.i("onCreateView");
        if (this.mRootView == null) {
            setContentView(inflater, container);
            this.initView();
            this.initListener();
        }
        return this.mRootView;
    }

    protected void setContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.mRootView = inflater.inflate(this.getContentViewId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LogUtil.i("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        this.mIsViewPrepared = true;
        this.isDestroyed = false;
        this.lazyLoadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            LogUtil.i("isVisibleToUser");
            this.lazyLoadData();
        }
    }

    @Override
    public void onDestroyView() {
        LogUtil.i("onDestroyView");
        super.onDestroyView();
        if (this.mRootView != null && this.mRootView.getParent() != null) {
            ((ViewGroup) this.mRootView.getParent()).removeView(this.mRootView);
        }
        this.mIsViewPrepared = false;
    }

    @Override
    public void onDestroy() {
        LogUtil.i("onDestroy");
        super.onDestroy();
        destroyEventBus();
        destroyNetMonitor();
        ClickUtil.clear();
        isDestroyed = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * 懒加载数据
     */
    private void lazyLoadData() {
        LogUtil.i("lazyLoadData");
        if (super.getUserVisibleHint() && !this.mHasLoadData && this.mIsViewPrepared) {
            this.mHasLoadData = true;
            LogUtil.i("initData");
            this.initData();
        }
    }

    protected void showKeyboard(boolean isShow) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        if (isShow) {
            if (activity.getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(activity.getCurrentFocus(), 0);
            }
        } else {
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected void hideKeyboard(View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public void onError(int code, String message) {
        LogUtil.e("code:" + code + " message:" + message);
    }

    /**
     * 网络状态变化回调
     *
     * @param isNetAvailable 网络是否可用
     */
    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        LogUtil.i("网络是否可用：" + isNetAvailable);
    }

    /**
     * 接收事件
     *
     * @param event
     * @param <T>
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public <T> void onEventBusCome(Event<T> event) {
        if (!isDestroyed() && event != null) {
            onMessageEvent(event.eventName, event.data);
        }
    }

    /**
     * 接收粘性事件
     *
     * @param event
     * @param <T>
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public <T> void onStickyEventBusCome(Event<T> event) {
        if (!isDestroyed() && event != null) {
            onMessageStickyEvent(event.eventName, event.data);
        }
    }

    /**
     * 接收普通事件
     */
    @Override
    public <T> void onMessageEvent(String eventName, T data) {

    }

    /**
     * 接收粘性事件
     */
    @Override
    public <T> void onMessageStickyEvent(String eventName, T data) {

    }
}
