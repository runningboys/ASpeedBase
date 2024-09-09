package com.common.utils.resource;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.common.base.BaseApp;
import com.common.utils.log.LogUtil;

import java.util.List;

/**
 * 应用程序工具类
 *
 * @author LiuFeng
 * @date 2018-8-15
 */
public class AppUtil {

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug() {
        try {
            ApplicationInfo info = BaseApp.context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = BaseApp.context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(BaseApp.context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return BaseApp.context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回app版本名字
     * 对应build.gradle中的versionName
     *
     * @return
     */
    public static String getVersionName() {
        String versionName = null;
        try {
            PackageManager packageManager = BaseApp.context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(BaseApp.context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 返回app版本号
     * 对应build.gradle中的versionCode
     *
     * @return
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            PackageManager packageManager = BaseApp.context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(BaseApp.context.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取指定包名应用的版本号
     *
     * @param packageName
     * @return
     */
    public static int getVersionCode(String packageName) {
        int versionCode = 0;
        try {
            PackageManager packageManager = BaseApp.context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示应用详细信息页面
     */
    public static void showInstalledAppDetails() {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", BaseApp.context.getPackageName(), null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(appPkgName, BaseApp.context.getPackageName());
        }
        BaseApp.context.startActivity(intent);
    }

    /**
     * 获取当前App进程的id
     *
     * @return
     */
    public static int getAppProcessId() {
        return android.os.Process.myPid();
    }

    /**
     * 判断是否主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        boolean mainProcess = false;
        try {
            String packageName = context.getPackageName();
            packageName = context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.processName;
            String processName = getCurrentProcessName(context);
            if (!TextUtils.isEmpty(packageName) && packageName.equalsIgnoreCase(processName)) {
                mainProcess = true;
            }
        } catch (Throwable e) {
            LogUtil.e("isMainProcess:get process name failed.", e);
        }

        return mainProcess;
    }

    /**
     * 获取当前进程名
     */
    public static String getCurrentProcessName(Context context) {
        int currentPid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
            if (process.pid == currentPid) {
                processName = process.processName;
                break;
            }
        }

        return processName;
    }

    /**
     * 应用是否处于后台
     */
    public static boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) BaseApp.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(BaseApp.context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    /**
     * 获取当前系统Launcher
     *
     * @return
     */
    public static String getLauncherName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = BaseApp.context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            return resolveInfo.activityInfo.packageName;
        } else {
            return "";
        }
    }

    /**
     * 获取内部资源
     *
     * @return
     */
    public static Resources getResources() {
        return BaseApp.getContext().getResources();
    }

    /**
     * 获取应用包名
     *
     * @return
     */
    public static String getPackageName() {
        return BaseApp.getContext().getPackageName();
    }

    /**
     * 创建主题
     *
     * @param styleRes
     * @return
     */
    public static Resources.Theme createTheme(int styleRes) {
        Resources.Theme theme = getResources().newTheme();
        theme.applyStyle(styleRes, true);
        return theme;
    }

    /**
     * 判断黑暗模式
     *
     * @return
     */
    public static boolean isDarkMode() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
