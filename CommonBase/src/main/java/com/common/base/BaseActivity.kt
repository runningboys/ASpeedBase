package com.common.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.common.base.ability.IBaseView
import com.common.base.ability.IEventBus
import com.common.base.ability.INetMonitor
import com.common.utils.eventbus.Event
import com.common.utils.log.LogUtil
import com.common.utils.ui.ClickUtil
import com.common.utils.ui.ToastUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 基础的activity
 */
abstract class BaseActivity : AppCompatActivity(), IEventBus, INetMonitor, IBaseView {
    private var mDestroyed = false
    protected lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEventBus()
        createNetMonitor()
        setContentView(getLayoutId())
        mContext = this
        initToolBar()
        initView()
        initListener()
        initData()
    }

    /**
     * 获取布局文件id
     *
     * @return Id
     */
    protected abstract fun getLayoutId(): Int

    /**
     * 初始化组件
     */
    protected abstract fun initView()

    /**
     * 初始化监听器
     */
    protected abstract fun initListener()

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 初始化toolbar
     */
    protected fun initToolBar() {}

    override fun showLoading() {}

    override fun hideLoading() {}

    override fun showMessage(message: String) {
        ToastUtil.showToast(message)
    }

    override fun onError(code: Int, message: String?) {
        LogUtil.e("code:$code message:$message")
    }

    override fun isDestroyed(): Boolean {
        return if (Build.VERSION.SDK_INT >= 17) {
            super.isDestroyed()
        } else {
            mDestroyed || super.isFinishing()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyEventBus()
        destroyNetMonitor()
        ClickUtil.clear()
        mDestroyed = true
    }

    override fun openMonitor(): Boolean {
        return false
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
    fun <T> onEventBusCome(event: Event<T>) {
        if (!isDestroyed) {
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
    fun <T> onStickyEventBusCome(event: Event<T>) {
        if (!isDestroyed) {
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