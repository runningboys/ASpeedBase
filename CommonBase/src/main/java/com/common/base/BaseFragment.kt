package com.common.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.common.base.ability.IBaseView
import com.common.base.ability.IEventBus
import com.common.base.ability.INetMonitor
import com.common.eventbus.Event
import com.common.utils.ClickUtil
import com.common.utils.log.LogUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Fragment基类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
abstract class BaseFragment : Fragment(), IEventBus, INetMonitor, IBaseView, View.OnClickListener {
    protected var mRootView: View? = null
    protected var mActivity: Activity? = null

    //fragment布局资源id
    var containerId = 0

    // 标识fragment视图已经初始化完毕
    private var mIsViewPrepared = false

    // 标识已经触发过懒加载数据
    private var mHasLoadData = false
    var isDestroyed = false
        private set

    /**
     * 获取布局文件id
     *
     * @return
     */
    protected abstract val contentViewId: Int

    /**
     * 初始化组件
     */
    protected fun initView() {}

    /**
     * 初始化监听器
     */
    protected fun initListener() {}

    /**
     * 初始化数据
     */
    protected fun initData() {}

    /**
     * 点击事件
     *
     * @param v
     */
    override fun onClick(v: View) {}
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.i("onCreate")
        super.onCreate(savedInstanceState)
        createEventBus()
        createNetMonitor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.i("onCreateView")
        if (mRootView == null) {
            setContentView(inflater, container)
            initView()
            initListener()
        }
        return mRootView
    }

    protected open fun setContentView(inflater: LayoutInflater, container: ViewGroup?) {
        mRootView = inflater.inflate(contentViewId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.i("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        mIsViewPrepared = true
        isDestroyed = false
        lazyLoadData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            LogUtil.i("isVisibleToUser")
            lazyLoadData()
        }
    }

    override fun onDestroyView() {
        LogUtil.i("onDestroyView")
        super.onDestroyView()
        if (mRootView != null && mRootView!!.parent != null) {
            (mRootView!!.parent as ViewGroup).removeView(mRootView)
        }
        mIsViewPrepared = false
    }

    override fun onDestroy() {
        LogUtil.i("onDestroy")
        super.onDestroy()
        destroyEventBus()
        destroyNetMonitor()
        ClickUtil.clear()
        isDestroyed = true
    }

    /**
     * 懒加载数据
     */
    private fun lazyLoadData() {
        LogUtil.i("lazyLoadData")
        if (super.getUserVisibleHint() && !mHasLoadData && mIsViewPrepared) {
            mHasLoadData = true
            LogUtil.i("initData")
            initData()
        }
    }

    protected fun showKeyboard(isShow: Boolean) {
        val activity = activity ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        if (isShow) {
            if (activity.currentFocus == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            } else {
                imm.showSoftInput(activity.currentFocus, 0)
            }
        } else {
            if (activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    protected fun hideKeyboard(view: View) {
        val activity = activity ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun showLoading() {}
    override fun hideLoading() {}
    override fun showMessage(message: String) {}
    override fun onError(code: Int, message: String?) {
        LogUtil.e("code:$code message:$message")
    }

    /**
     * 网络状态变化回调
     *
     * @param isNetAvailable 网络是否可用
     */
    override fun onNetworkStateChanged(isNetAvailable: Boolean) {
        LogUtil.i("网络是否可用：$isNetAvailable")
    }

    /**
     * 接收事件
     *
     * @param event
     * @param <T>
    </T> */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun <T> onEventBusCome(event: Event<T>?) {
        if (!isDestroyed && event != null) {
            onMessageEvent(event.eventName, event.data)
        }
    }

    /**
     * 接收粘性事件
     *
     * @param event
     * @param <T>
    </T> */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun <T> onStickyEventBusCome(event: Event<T>?) {
        if (!isDestroyed && event != null) {
            onMessageStickyEvent(event.eventName, event.data)
        }
    }

    /**
     * 接收普通事件
     */
    override fun <T> onMessageEvent(eventName: String, data: T) {}

    /**
     * 接收粘性事件
     */
    override fun <T> onMessageStickyEvent(eventName: String, data: T) {}
}