package com.util.base.skin;

import android.app.Activity;
import android.content.Intent;

/**
 * 皮肤测试
 *
 * @author LiuFeng
 * @data 2021/9/23 11:57
 */
public class SkinTest {

    public static void runTest(Activity activity) {
        UIManager.instanceUI();

        Intent intent = new Intent(activity, TestSkinActivity.class);
        activity.startActivity(intent);
    }
}
