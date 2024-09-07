package com.common.utils.resource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.util.TypedValue;

import com.common.base.BaseApp;

import java.util.UUID;

/**
 * 设备工具类
 *
 * @author LiuFeng
 * @date 2018-8-15
 */
public class DeviceUtil {


    /**
     * 获取设备的唯一标识，deviceId
     *
     * @param context
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            //有些山寨机会写死androidId。
            if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
                androidId = UUID.nameUUIDFromBytes(androidId.getBytes("utf-8")).toString();
            }
            else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    androidId = UUID.randomUUID().toString();
                }
                else {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String telephonyManagerDeviceId = telephonyManager.getDeviceId();
                    androidId = telephonyManagerDeviceId != null ? UUID.nameUUIDFromBytes(telephonyManagerDeviceId.getBytes("utf-8")).toString() : UUID.randomUUID().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            androidId = UUID.randomUUID().toString();
        }

        return androidId;
    }

    /**
     * 获取设备的唯一标识，deviceId
     *
     * @param context
     *
     * @return
     */
    public static String getTelephonyDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取手机制造商
     *
     * @return
     */
    public static String getPhoneManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机CPU
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String[] getPhoneCPU() {
        return Build.SUPPORTED_ABIS;
    }

    /**
     * 获取手机Android API等级（如：22、23 ...）
     *
     * @return
     */
    public static int getBuildLevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android版本（如：4.4、5.0、5.1 ...）
     *
     * @return
     */
    public static String getBuildVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceInfo() {
        String handSetInfo = "手机型号：" + Build.DEVICE + "\n系统版本：" + Build.VERSION.RELEASE + "\nSDK版本：" + Build.VERSION.SDK_INT;
        return handSetInfo;
    }

    private static String getDeviceModel() {
        return Build.DEVICE;
    }

    /**
     * dp转px
     *
     *
     * @return
     */

    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getContext().getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     *
     * @return
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getContext().getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal
     *
     * @return
     */

    public static float px2dp(float pxVal) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     *
     * @return
     */
    public static float px2sp(float pxVal) {
        return (pxVal / getContext().getResources().getDisplayMetrics().scaledDensity);
    }


    private static Context getContext() {
        return BaseApp.getContext();
    }
}
