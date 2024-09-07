package com.common.skin.base;

import android.os.Bundle;

import com.common.base.R;
import com.common.skin.api.SkinHelper;
import com.common.skin.api.SkinItem;
import com.common.skin.api.SkinManager;
import com.common.skin.api.SkinType;
import com.common.skin.callback.SkinChangeListener;
import com.common.utils.ui.StatusBarUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 皮肤基类Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 18:23
 */
public abstract class SkinActivity extends AppCompatActivity implements SkinChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SkinManager.getInstance().register(this);
        SkinManager.getInstance().addListener(this);
        SkinManager.getInstance().loadActivityXml(this);
        setStatusBar(SkinManager.getInstance().getCurrentSkin());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unRegister(this);
        SkinManager.getInstance().removeListener(this);
    }

    @Override
    public void onSkinChange(SkinItem oldSkin, SkinItem newSkin) {
        setStatusBar(newSkin);
    }

    /**
     * 设置状态栏皮肤
     *
     * @param newSkin
     */
    private void setStatusBar(SkinItem newSkin) {
        // 调状态栏字体
        boolean isLightMode = SkinType.SKIN_WHITE.name().equals(newSkin.getSkin());
        StatusBarUtil.setStatusBarLightMode(this, isLightMode);

        // 调状态栏背景色
        int color = getStatusBarColor(newSkin, R.color.skin_app_primary_color);
        StatusBarUtil.setStatusBar(this, color);
    }

    /**
     * 根据皮肤和资源获取状态栏颜色
     *
     * @param skin
     * @param colorResId
     * @return
     */
    private int getStatusBarColor(SkinItem skin, int colorResId) {
        if (skin.isPlugin()) {
            int resId = SkinHelper.getResourceManager().getRealResId(colorResId);
            return SkinHelper.getResourceManager().getColor(resId);
        } else {
            return SkinHelper.getResourceManager().getColor(skin.getTheme(), colorResId);
        }
    }
}
