package com.common.utils.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.common.base.R

/**
 * glide工具类
 */
object GlideUtil {
    private const val defaultRoundRadius = 4


    /**
     * 加载图片
     */
    fun loadImage(context: Context, view: ImageView, data: Any, defResId: Int = 0) {
        reqBuilder(context, data, defResId).into(view)
    }


    /**
     * 加载图片
     */
    fun loadImage(context: Context, view: ImageView, data: Any, width: Int, height: Int, defResId: Int = 0) {
        reqBuilder(context, data, defResId).override(width, height).into(view)
    }


    /**
     * 加载GIF
     */
    fun loadGif(context: Context, view: ImageView, data: Any, defResId: Int = 0) {
        reqBuilder(context, data, defResId).diskCacheStrategy(DiskCacheStrategy.DATA).into(view)
    }


    /**
     * 加载圆形图片
     */
    fun loadCircleImage(context: Context, view: ImageView, data: Any, defResId: Int = 0) {
        loadImage(context, view, data, CircleCrop(), defResId)
    }


    /**
     * 加载圆角图片，并设置成CenterCrop
     */
    fun loadRoundImageWithCenterCrop(context: Context, view: ImageView, data: Any, roundRadius: Int, defResId: Int = 0) {
        loadImage(context, view, data, MultiTransformation(CenterCrop(), RoundedCorners(roundRadius)), defResId)
    }

    /**
     * 加载圆角图片
     */
    fun loadRoundImage(context: Context, view: ImageView, data: Any, roundRadius: Int, defResId: Int = 0) {
        val radius = if (roundRadius < 0) defaultRoundRadius else roundRadius
        loadImage(context, view, data, RoundedCorners(radius), defResId)
    }

    /**
     * 加载模糊图片
     */
    fun loadBlurImage(context: Context, view: ImageView, data: Any, defResId: Int = 0) {
        loadImage(context, view, data, GlideBlurTransformation(context), defResId)
    }

    /**
     * 加载旋转图片
     */
    fun loadRotateImage(context: Context, view: ImageView, data: Any, rotateAngle: Int, defResId: Int = 0) {
        loadImage(context, view, data, Rotate(rotateAngle), defResId)
    }


    private fun loadImage(context: Context, view: ImageView, data: Any, transform: Transformation<Bitmap>, defResId: Int = 0) {
        reqBuilder(context, data, defResId).transform(transform).into(view)
    }


    private fun reqBuilder(context: Context, data: Any, defResId: Int = 0): RequestBuilder<Drawable> {
        return Glide.with(context).load(data).placeholder(defResId).error(R.drawable.ic_default_img_failed)
    }




    /**
     * 清除内存中的缓存 必须在UI线程中调用
     */
    fun clearMemory(context: Context) {
        Glide.get(context).clearMemory()
    }

    /**
     * 清除磁盘中的缓存 必须在后台线程中调用，建议同时clearMemory()
     */
    fun clearDiskCache(context: Context) {
        Glide.get(context).clearDiskCache()
    }
}