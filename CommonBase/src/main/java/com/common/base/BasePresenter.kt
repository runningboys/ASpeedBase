package com.common.base

import android.content.Context
import com.common.base.ability.IBaseView
import com.common.utils.NetworkUtil
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * presenter基类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
abstract class BasePresenter<V : IBaseView>(
        /**
         * 上下文
         */
        protected var mContext: Context?,

        /**
         * 绑定的view
         */
        protected var mView: V?
) {
    /**
     * 综合订阅管理
     */
    private var mCompositeSubscription: CompositeSubscription? = null

    /**
     * view是否已绑定
     *
     * @return
     */
    protected val isAttachView: Boolean = mView != null

    /**
     * 网络是否可用
     *
     * @return
     */
    protected val isNetAvailable: Boolean = NetworkUtil.isNetAvailable()

    /**
     * 添加订阅
     *
     * @param subscription
     */
    protected fun addSubscribe(subscription: Subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = CompositeSubscription()
        }
        mCompositeSubscription?.add(subscription)
    }

    /**
     * 取消一个订阅
     *
     * @param subscription
     */
    protected fun unSubscribe(subscription: Subscription) {
        if (mCompositeSubscription != null && mCompositeSubscription!!.hasSubscriptions()) {
            mCompositeSubscription!!.remove(subscription)
        }
    }

    /**
     * 取消所有订阅
     */
    protected fun unSubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription!!.hasSubscriptions()) {
            mCompositeSubscription?.clear()
        }
    }

    /**
     * 销毁
     */
    fun onDestroy() {
        unSubscribe()
        mContext = null
        mView = null
    }
}