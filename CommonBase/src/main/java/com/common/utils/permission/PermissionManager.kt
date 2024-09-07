package com.common.utils.permission

import android.Manifest
import android.os.Build
import com.blankj.utilcode.util.PermissionUtils
import com.common.utils.resource.ServiceUtil


/**
 * 动态权限管理
 */
object PermissionManager {
    private var audioPermission = Manifest.permission.RECORD_AUDIO

    private var cameraAndAudioPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    )

    private var storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    ) else arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )



    /**
     * 判断存储权限
     */
    fun hasStoragePermission(): Boolean {
        return hasPermissions(*storagePermissions)
    }


    /**
     * 判断摄像头和录音权限
     */
    fun hasCameraAndAudioPermission(): Boolean {
        return hasPermissions(*cameraAndAudioPermissions)
    }


    /**
     * 判断录音权限
     */
    fun hasAudioPermission(): Boolean {
        return hasPermissions(audioPermission)
    }


    /**
     * 判断发系统通知权限
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermissions(Manifest.permission.POST_NOTIFICATIONS)
        } else true
    }


    /**
     * 通知启用
     */
    fun isNotificationEnable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ServiceUtil.getNotificationManager().areNotificationsEnabled()
        } else true
    }


    fun requestAudioPermission(onFail: ((doNotAskAgain: Boolean) -> Unit)? = null, onSuccess: () -> Unit) {
        requestPermissions(audioPermission, onFail = onFail, onSuccess = onSuccess)
    }

    fun requestCameraAndAudioPermissions(onFail: ((doNotAskAgain: Boolean) -> Unit)? = null, onSuccess: () -> Unit) {
        requestPermissions(*cameraAndAudioPermissions, onFail = onFail, onSuccess = onSuccess)
    }


    fun requestStoragePermissions(onFail: ((doNotAskAgain: Boolean) -> Unit)? = null, onSuccess: () -> Unit) {
        requestPermissions(*storagePermissions, onFail = onFail, onSuccess = onSuccess)
    }

    fun requestNotificationPermissions(onFail: ((doNotAskAgain: Boolean) -> Unit)? = null, onSuccess: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(Manifest.permission.POST_NOTIFICATIONS, onFail = onFail, onSuccess = onSuccess)
        }
    }



    /**
     * 判断权限
     */
    fun hasPermissions(vararg perms: String): Boolean {
        return PermissionUtils.isGranted(*perms)
    }


    /**
     * 请求权限
     */
    fun requestPermissions(vararg perms: String, onFail: ((doNotAskAgain: Boolean) -> Unit)?, onSuccess: () -> Unit, ) {
        PermissionUtils.permission(*perms).callback { isAllGranted, granted, deniedForever, denied ->
            if (isAllGranted) {
                onSuccess()
            } else {
                onFail?.invoke(deniedForever.isNotEmpty())
            }
        }.request()
    }

}