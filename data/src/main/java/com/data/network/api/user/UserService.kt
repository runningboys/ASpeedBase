package com.data.network.api.user

import com.common.data.NetResult
import com.data.network.api.BaseService
import com.data.network.manager.ApiManager
import com.data.network.model.UserBean
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * 用户请求服务
 * 实现并封装api接口服务
 */
object UserService: BaseService() {
    private val mApiService by lazy { ApiManager.defaultInstance().getApiService(UserApi::class.java) }


    /**
     * 登录
     */
    fun login(phone: String, password: String): Observable<NetResult<UserBean>> {
        val params = mutableMapOf<String, String>()
        params["phone"] = phone
        params["password"] = password
        return mApiService.login(params).subscribeOn(Schedulers.io())
    }


    /**
     * 登录
     */
    suspend fun login2(phone: String, password: String): NetResult<UserBean> {
        val params = mutableMapOf<String, String>()
        params["phone"] = phone
        params["password"] = password
        return handleResult { mApiService.login2(params) }
    }

}