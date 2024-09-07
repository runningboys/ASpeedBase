package com.data.network.manager.interceptor

import com.common.data.NoNetworkException
import com.common.utils.network.NetworkUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 监测网络状态拦截器
 */
class NetworkStateInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        // 无网络处理
        if (!NetworkUtil.isNetAvailable()) {
            throw NoNetworkException()
        }

        return chain.proceed(builder.build())
    }
}