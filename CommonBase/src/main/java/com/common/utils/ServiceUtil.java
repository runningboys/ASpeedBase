package com.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.job.JobScheduler;
import android.content.ClipboardManager;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.common.CommonUtil;

public class ServiceUtil {
    private static Context getContext() {
        return CommonUtil.getContext();
    }

    public static InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE);
    }

    public static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getContext().getSystemService(Activity.CONNECTIVITY_SERVICE);
    }

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static AudioManager getAudioManager() {
        return (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public static PowerManager getPowerManager() {
        return (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
    }

    public static AlarmManager getAlarmManager() {
        return (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    public static Vibrator getVibrator() {
        return (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static DisplayManager getDisplayManager() {
        return (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);
    }

    public static AccessibilityManager getAccessibilityManager() {
        return (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static ClipboardManager getClipboardManager() {
        return (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static JobScheduler getJobScheduler() {
        return (JobScheduler) getContext().getSystemService(JobScheduler.class);
    }

    public static SubscriptionManager getSubscriptionManager() {
        return (SubscriptionManager) getContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
    }

    public static ActivityManager getActivityManager() {
        return (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static LocationManager getLocationManager() {
        return ContextCompat.getSystemService(getContext(), LocationManager.class);
    }

}
