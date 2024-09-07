package com.common.utils.glide


import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream


/**
 * Glide加载https图片,忽略ssl证书验证
 */
@GlideModule
class OkHttpAppGlideModule: AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = SslSocketClient.getOkHttpClient()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
