package com.common.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.common.base.ability.IEventBus
import com.common.base.ability.INetMonitor
import com.common.base.ability.IBaseView
import android.os.Bundle
import androidx.annotation.LayoutRes
import com.common.utils.ClickUtil
import com.common.utils.ToastUtil
import com.common.utils.log.LogUtil
import com.common.utils.UIHandler
import com.common.base.BaseFragment
import com.umeng.analytics.MobclickAgent
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.common.eventbus.Event
import com.common.manager.ActivityManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.ArrayList

/**
 * 基础的activity
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
abstract class BaseActivity : AppCompatActivity(), IEventBus, INetMonitor, IBaseView, View.OnClickListener {
    protected var mSavedInstanceState: Bundle? = null
    private var mHandler: Handler? = null
    private var mDestroyed = false
    protected var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEventBus()
        createNetMonitor()
        ActivityManager.getInstance().addActivity(this)
        setContentView(getContentViewId())
        mContext = this
        initToolBar()
        initView()
        initListener()
        mSavedInstanceState = savedInstanceState
        initData()
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        if (layoutResID != 0) {
            super.setContentView(layoutResID)
        }
    }

    /**
     * 获取布局文件id
     *
     * @return Id
     */
    protected abstract fun getContentViewId(): Int

    /**
     * 初始化数据
     */
    protected open fun initData() {}

    /**
     * 初始化toolbar
     */
    protected fun initToolBar() {}

    /**
     * 初始化组件
     */
    protected fun initView() {}

    /**
     * 初始化监听器
     */
    protected fun initListener() {}

    /**
     * 点击事件
     *
     * @param v 点击事件view
     */
    override fun onClick(v: View) {
        // 去抖动点击处理
        if (ClickUtil.isNormalClick(v)) {
            onNormalClick(v)
        }
    }

    /**
     * 去抖动后的正常点击事件
     *
     * @param v 点击事件view
     */
    fun onNormalClick(v: View) {}

    override fun showLoading() {}

    override fun hideLoading() {}

    override fun showMessage(message: String) {
        ToastUtil.showToast(message)
    }

    override fun onError(code: Int, message: String?) {
        LogUtil.e("code:$code message:$message")
    }

    /**
     * 获取主Handler
     *
     * @return 主线程Handler
     */
    protected val handler: Handler?
        protected get() {
            if (mHandler == null) {
                mHandler = UIHandler.getInstance()
            }
            return mHandler
        }

    /**
     * 是否需要弹出键盘
     *
     * @param isShow true为弹出键盘
     */
    protected fun showKeyboard(isShow: Boolean) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) {
            if (currentFocus == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            } else {
                imm.showSoftInput(currentFocus, 0)
            }
        } else {
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    /**
     * 延时弹出键盘
     *
     * @param focus 键盘的焦点view
     */
    protected fun showKeyboardDelayed(focus: View?) {
        focus?.requestFocus()
        handler!!.postDelayed({
            if (focus == null || focus.isFocused) {
                showKeyboard(true)
            }
        }, 200)
    }

    /**
     * 添加一个fragment
     *
     * @param fragment
     * @return
     */
    fun addFragment(fragment: BaseFragment): BaseFragment? {
        val fragments: MutableList<BaseFragment> = ArrayList(1)
        fragments.add(fragment)
        val fragmentList = addFragmentList(fragments)
        return fragmentList[0]
    }

    /**
     * 添加fragment列表
     *
     * @param fragments
     * @return
     */
    fun addFragmentList(fragments: List<BaseFragment>): List<BaseFragment?> {
        val fragmentList: MutableList<BaseFragment?> = ArrayList(fragments.size)
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        var commit = false
        for (fragment in fragments) {
            val containerId = fragment.containerId
            var fragment2 = fm.findFragmentById(containerId) as BaseFragment?
            if (fragment2 == null) {
                fragment2 = fragment
                transaction.add(containerId, fragment)
                commit = true
            }
            fragmentList.add(fragment2)
        }
        if (commit) {
            try {
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return fragmentList
    }

    /**
     * 切换fragment
     *
     * @param fragment
     * @return
     */
    fun <T : BaseFragment?> switchContent(fragment: T): T {
        return switchContent(fragment, false, null)
    }

    /**
     * 切换fragment
     *
     * @param fragment
     * @param needAddToBackStack 是否需要添加到返回栈
     * @param tag
     * @return
     */
    protected fun <T : BaseFragment?> switchContent(fragment: T, needAddToBackStack: Boolean, tag: String?): T {
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        if (tag != null) {
            fragmentTransaction.replace(fragment!!.containerId, fragment, tag)
        } else {
            fragmentTransaction.replace(fragment!!.containerId, fragment)
        }
        if (needAddToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        try {
            fragmentTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fragment
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    protected fun switchFragmentContent(fragment: BaseFragment) {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(fragment.containerId, fragment)
        try {
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun isDestroyed(): Boolean {
        return if (Build.VERSION.SDK_INT >= 17) {
            super.isDestroyed()
        } else {
            mDestroyed || super.isFinishing()
        }
    }

    override fun onDestroy() {
        ActivityManager.getInstance().finishActivity(this)
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