package com.util.base.ui.main

import com.common.base.binding.BindingActivity
import com.util.base.databinding.ActivityMainBinding
import com.util.base.ui.NavigatorUtil


class MainActivity : BindingActivity<ActivityMainBinding>() {

    override fun initView() {

    }

    override fun initListener() {
        binding.titleTv.setOnClickListener {
            NavigatorUtil.toOne(this)
        }
    }

    override fun initData() {

    }
}