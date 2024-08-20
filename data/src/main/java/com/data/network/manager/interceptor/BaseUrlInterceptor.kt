package com.data.network.manager.interceptor

import com.common.utils.log.LogUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * BaseUrl拦截处理
 */
class BaseUrlInterceptor(val getBaseUrl: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldHttpUrl = request.url
        val oldUrl = oldHttpUrl.toString()


        // 当请求域名不同时，替换域名
        val baseUrl = getBaseUrl()
        if (!oldUrl.startsWith(baseUrl)) {
            val newBaseUrl = baseUrl.toHttpUrlOrNull()
            if (newBaseUrl != null) {
                val newFullUrl = oldHttpUrl.newBuilder()
                        .scheme(newBaseUrl.scheme)  //更换网络协议
                        .host(newBaseUrl.host)      //更换主机名
                        .port(newBaseUrl.port)      //更换端口
                        .build()
                LogUtil.i("url intercept: $newFullUrl")
                return chain.proceed(request.newBuilder().url(newFullUrl).build())
            }
        }

        return chain.proceed(request)
    }
}