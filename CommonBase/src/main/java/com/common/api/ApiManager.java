package com.bctalk.global.model.api;


import android.text.TextUtils;

import com.bctalk.framework.utils.AppUtils;
import com.bctalk.framework.utils.log.LogUtil;
import com.bctalk.global.AppManager;
import com.bctalk.global.network.GsonConverterFactory;
import com.bctalk.global.network.RFNetUtil;
import com.bctalk.global.network.data.NoNetworkException;
import com.bctalk.global.network.interceptor.CacheControlInterceptor;
import com.bctalk.global.network.interceptor.HeadersInterceptor;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * api网络请求管理器
 *
 * @author LiuFeng
 * @data 2020/2/3 18:23
 */
public class ApiManager {
    private static final String TAG = "ApiManager";

    private static final int READ_TIME_OUT = 20;      // 读取超时时间：单位秒
    private static final int CONNECT_TIME_OUT = 20;   // 连接超时时间：单位秒

    private OkHttpClient mOkHttpClient;  // OkHttpClient实例

    private volatile String BASE_URL = AppManager.getBaseUrl();

    private final Map<String, Retrofit> retrofitMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> apiServiceMap = new ConcurrentHashMap<>();

    /**
     * 单例
     */
    public static ApiManager getInstance() {
        return ApiManagerHolder.mInstance;
    }

    private static class ApiManagerHolder {
        private static final ApiManager mInstance = new ApiManager();
    }

    /**
     * 构造方法
     */
    private ApiManager() {
        this.initOkHttp();
    }

    /**
     * 初始化OkHttp
     */
    private void initOkHttp() {
        // 指定缓存路径,缓存大小100Mb
        Cache cache = new Cache(new File(AppUtils.getApplication().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        setSSL(builder);                                            // 忽略证书验证
        builder.cache(cache);                                       // 指定缓存
        builder.addInterceptor(new HeadersInterceptor());           // 添加公共请求头
        builder.addInterceptor(new CacheControlInterceptor());      // 缓存控制
        builder.addInterceptor(getLogInterceptor());                // 打印请求log日志
        builder.addInterceptor(getNetworkInterceptor());            // 网络状态拦截处理
        builder.addInterceptor(getBaseUrlInterceptor());            // BaseUrl拦截替换
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS); // 设置连接超时
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);       // 设置读取超时
        builder.writeTimeout(READ_TIME_OUT, TimeUnit.SECONDS);      // 设置写入超时
        builder.retryOnConnectionFailure(true);                     // 设置重连

        this.mOkHttpClient = builder.build();
    }

    /**
     * 设置忽略ssl证书验证
     *
     * @param builder
     */
    private void setSSL(OkHttpClient.Builder builder) {
        try {
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            HostnameVerifier doNotVerify = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            builder.sslSocketFactory(sslContext.getSocketFactory(), xtm);
            builder.hostnameVerifier(doNotVerify);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 日志拦截
     *
     * @return
     */
    private Interceptor getLogInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                int printMaxLength = 10000;
                if (message.length() < printMaxLength) {
                    LogUtil.i("OkHttp:", message);
                } else {
                    LogUtil.i("OkHttp:", message.substring(0, printMaxLength));
                }
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * 网络状态拦截
     *
     * @return
     */
    private Interceptor getNetworkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                // 无网络处理
                if (!RFNetUtil.isNetworkConnected()) {
                    throw new NoNetworkException();
                }

                return chain.proceed(builder.build());
            }
        };
    }

    /**
     * BaseUrl拦截处理
     *
     * @return
     */
    private Interceptor getBaseUrlInterceptor() {
        return new Interceptor() {
            @Override
            public @NotNull Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl oldHttpUrl = request.url();
                String oldUrl = oldHttpUrl.toString();

                // 当请求域名不同、且非健康检查接口时，替换域名
                if (!oldUrl.startsWith(BASE_URL) && !oldUrl.contains("system/admin/public/health")) {
                    HttpUrl newBaseUrl = HttpUrl.parse(BASE_URL);
                    if (newBaseUrl != null) {
                        HttpUrl newFullUrl = oldHttpUrl.newBuilder() //
                                .scheme(newBaseUrl.scheme())         //更换网络协议
                                .host(newBaseUrl.host())             //更换主机名
                                .port(newBaseUrl.port())             //更换端口
                                .build();

                        LogUtil.i(TAG, "url intercept: " + newFullUrl.toString());
                        return chain.proceed(request.newBuilder().url(newFullUrl).build());
                    }
                }

                return chain.proceed(request);
            }
        };
    }

    /**
     * 更新url地址
     *
     * @param baseUrl
     */
    public void updateBaseUrl(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    /**
     * 获取ApiService
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getApiService(Class<T> clazz) {
        return getApiService(clazz, BASE_URL);
    }

    /**
     * 获取ApiService
     * 备注：支持多api的调用
     *
     * @param clazz
     * @param baseUrl
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getApiService(Class<T> clazz, String baseUrl) {
        if (!apiServiceMap.containsKey(clazz)) {
            synchronized (this) {
                if (!apiServiceMap.containsKey(clazz)) {
                    Retrofit retrofit = getRetrofit(baseUrl);
                    T service = retrofit.create(clazz);
                    apiServiceMap.put(clazz, service);
                    return service;
                }
            }
        }

        return (T) apiServiceMap.get(clazz);
    }

    /**
     * 初始化获取Retrofit
     */
    private Retrofit getRetrofit(String baseUrl) {
        Retrofit retrofit = retrofitMap.get(baseUrl);
        if (retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(baseUrl);  // base地址
            builder.client(this.mOkHttpClient);
            builder.addConverterFactory(GsonConverterFactory.create());
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            retrofit = builder.build();
            retrofitMap.put(baseUrl, retrofit);
        }

        return retrofit;
    }

    /**
     * 清空重置资源
     */
    public void clear() {
        retrofitMap.clear();
        apiServiceMap.clear();
        updateBaseUrl(AppManager.getBaseUrl());
    }
}
