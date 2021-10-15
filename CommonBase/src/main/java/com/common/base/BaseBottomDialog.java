package com.common.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * 基类底部弹窗
 *
 * @author LiuFeng
 * @data 2021/10/15 15:47
 */
public abstract class BaseBottomDialog extends DialogFragment {
    protected View mRootView;

    @Override
    public void onStart() {
        super.onStart();
        //得到dialog对应的window
        if (getDialog() != null && getDialog().getWindow() != null) {
            Dialog dialog = getDialog();
            Window window = dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            params.gravity = Gravity.BOTTOM; //修改gravity
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
            window.setAttributes(params);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.mRootView == null) {
            setContentView(inflater, container);
            this.initView();
            this.initListener();
        }
        return this.mRootView;
    }

    protected void setContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.mRootView = inflater.inflate(getLayoutId(), null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mRootView != null && this.mRootView.getParent() != null) {
            ((ViewGroup) this.mRootView.getParent()).removeView(this.mRootView);
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();
}
