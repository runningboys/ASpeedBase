package com.data.network.api.user

import com.common.data.NetResult
import com.data.network.model.UserBean
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 用户api接口
 */
interface UserApi {

    /**
     * 登录
     */
    @POST("/user/login")
    fun login(@Body params: Map<String, String>): Observable<UserBean>

    /**
     * 登录
     */
    @POST("/user/login")
    suspend fun login2(@Body params: Map<String, String>): NetResult<UserBean>
}