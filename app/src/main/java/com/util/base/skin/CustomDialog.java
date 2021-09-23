package com.util.base.skin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.common.skin.base.SkinDialog;

import androidx.annotation.LayoutRes;

/**
 * 自定义弹窗
 *
 * @author LiuFeng
 * @data 2021/9/9 17:22
 */
public class CustomDialog extends SkinDialog {

    public CustomDialog(Context context) {
        super(context);
    }


    public static class CustomBuilder {
        private int mLayoutId;
        private View view;
        private final Context mContext;

        public CustomBuilder(Context context) {
            mContext = context;
        }

        public CustomBuilder setContentView(@LayoutRes int layoutId) {
            this.mLayoutId = layoutId;
            return this;
        }

        public CustomBuilder setContentView(View view) {
            this.view = view;
            return this;
        }

        public SkinDialog create() {
            CustomDialog dialog = new CustomDialog(mContext);
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            }
            dialog.setContentView(view);
            return dialog;
        }
    }
}
