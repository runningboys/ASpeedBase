package com.util.base;

import com.common.base.binding.BindingActivity;
import com.util.base.databinding.ActivityMainBinding;

public class MainActivity extends BindingActivity<ActivityMainBinding> {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initData() {

    }
}