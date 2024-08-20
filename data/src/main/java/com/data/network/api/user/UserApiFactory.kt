package com.data.network.api.user

import com.data.network.api.model.UserBean
import com.data.network.manager.ApiManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * 用户网络请求api工厂
 */
object UserApiFactory {
    private val mApiService by lazy { ApiManager.defaultInstance().getApiService(UserApiService::class.java) }


    /**
     * 通过手机号注册
     */
    fun register(phone: String, password: String): Observable<UserBean> {
        val params = mutableMapOf<String, String>()
        params["phone"] = phone
        params["password"] = password
        return mApiService.register(params).subscribeOn(Schedulers.io())
    }

    /**
     * 密码登录
     */
    fun login(phone: String, password: String): Observable<UserBean> {
        val params = mutableMapOf<String, String>()
        params["phone"] = phone
        params["password"] = password
        return mApiService.login(params).subscribeOn(Schedulers.io())
    }

}