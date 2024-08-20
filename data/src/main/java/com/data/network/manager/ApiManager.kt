package com.data.network.manager

import com.common.CommonUtil
import com.common.utils.glide.SslSocketClient
import com.common.utils.log.LogUtil
import com.data.network.manager.interceptor.BaseUrlInterceptor
import com.data.network.manager.interceptor.HeadersInterceptor
import com.data.network.manager.interceptor.NetworkStateInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * api网络请求管理器
 *
 * @author LiuFeng
 * @data 2020/2/3 18:23
 */
class ApiManager private constructor() {
    private lateinit var mOkHttpClient: OkHttpClient // OkHttpClient实例

    private val READ_TIME_OUT = 20 // 读取超时时间：单位秒
    private val CONNECT_TIME_OUT = 20 // 连接超时时间：单位秒
    private var BASE_URL: String = ""
    private val retrofitMap: MutableMap<String, Retrofit?> = ConcurrentHashMap()
    private val apiServiceMap: MutableMap<Class<*>, Any> = ConcurrentHashMap()

    /**
     * 构造方法
     */
    init {
        initOkHttp()
    }

    /**
     * 初始化OkHttp
     */
    private fun initOkHttp() {
        // 指定缓存路径,缓存大小100Mb
        val cacheDir = CommonUtil.getContext().cacheDir
        val cache = Cache(File(cacheDir, "HttpCache"), (1024 * 1024 * 100).toLong())
        val builder = OkHttpClient.Builder()
        setSSL(builder) // 忽略证书验证
        builder.cache(cache) // 指定缓存
        builder.addInterceptor(HeadersInterceptor()) // 添加公共请求头
//        builder.addInterceptor(CacheControlInterceptor()) // 缓存控制
        builder.addInterceptor(logInterceptor) // 打印请求log日志
        builder.addInterceptor(NetworkStateInterceptor()) // 网络状态拦截处理
//        builder.addInterceptor(BaseUrlInterceptor { BASE_URL }) // BaseUrl拦截替换
        builder.connectTimeout(CONNECT_TIME_OUT.toLong(), TimeUnit.SECONDS) // 设置连接超时
        builder.readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS) // 设置读取超时
        builder.writeTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS) // 设置写入超时
        builder.retryOnConnectionFailure(true) // 设置重连
        mOkHttpClient = builder.build()
    }

    /**
     * 设置忽略ssl证书验证
     *
     * @param builder
     */
    private fun setSSL(builder: OkHttpClient.Builder) {
        try {
            builder.sslSocketFactory(SslSocketClient.getSSLSocketFactory(), SslSocketClient.getTrustManager())
            builder.hostnameVerifier(SslSocketClient.getHostnameVerifier())
        } catch (e: NoSuchAlgorithmException) {
            LogUtil.e(TAG, e)
        } catch (e: KeyManagementException) {
            LogUtil.e(TAG, e)
        }
    }

    private val logInterceptor: Interceptor
        get() {
            val loggingInterceptor = HttpLoggingInterceptor { message: String ->
                val printMaxLength = 10000
                if (message.length < printMaxLength) {
                    LogUtil.i("OkHttp:", message)
                } else {
                    LogUtil.i("OkHttp:", message.substring(0, printMaxLength))
                }
            }
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return loggingInterceptor
        }

    /**
     * 更新url地址
     *
     * @param baseUrl
     */
    fun updateBaseUrl(baseUrl: String) {
        BASE_URL = baseUrl
    }

    /**
     * 获取ApiService
     *
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> getApiService(clazz: Class<T>): T {
        return getApiService(clazz, BASE_URL)
    }

    /**
     * 获取ApiService
     * 备注：支持多api的调用
     *
     * @param clazz
     * @param baseUrl
     * @param <T>
     * @return
    </T> */
    fun <T> getApiService(clazz: Class<T>, baseUrl: String): T {
        if (!apiServiceMap.containsKey(clazz)) {
            synchronized(this) {
                if (!apiServiceMap.containsKey(clazz)) {
                    val retrofit = getRetrofit(baseUrl)
                    val service = retrofit.create(clazz)
                    apiServiceMap[clazz] = service!!
                    return service
                }
            }
        }
        return apiServiceMap[clazz] as T
    }

    /**
     * 初始化获取Retrofit
     */
    private fun getRetrofit(baseUrl: String): Retrofit {
        var retrofit = retrofitMap[baseUrl]
        if (retrofit == null) {
            val builder = Retrofit.Builder()
            builder.baseUrl(baseUrl)
            builder.client(mOkHttpClient)
            builder.addConverterFactory(GsonConverterFactory.create())
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            retrofit = builder.build()
            retrofitMap[baseUrl] = retrofit
        }
        return retrofit!!
    }

    /**
     * 清空重置资源
     */
    fun clear() {
        retrofitMap.clear()
        apiServiceMap.clear()
    }


    companion object {
        private const val TAG = "ApiManager"
        private const val DEFAULT_NAME = "default-manager"
        private val instanceMap = mutableMapOf<String, ApiManager>()

        /**
         * 获取默认实例
         *
         * @param context
         * @return
         */
        fun defaultInstance(): ApiManager {
            return of(DEFAULT_NAME)
        }

        /**
         * 获取对应实例
         *
         * @param context
         * @param name
         * @return
         */
        fun of(name: String): ApiManager {
            var instance = instanceMap[name]
            if (instance == null) {
                instance = ApiManager()
                instanceMap[name] = instance
            }
            return instance
        }
    }
}