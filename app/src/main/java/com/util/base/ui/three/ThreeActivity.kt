package com.util.base.ui.three

import androidx.activity.viewModels
import com.common.base.mvi.MviBindingActivity
import com.util.base.databinding.ActivityThreeBinding

class ThreeActivity : MviBindingActivity<ActivityThreeBinding, ThreeViewModel>() {
    override val viewModel: ThreeViewModel by viewModels()


    override fun initView() {
    }

    override fun initListener() {
    }

    override fun initData() {
        viewModel.observeUiState {
            when (it) {
                is ThreeUiState.Success -> {
                    println("success")
                }
                is ThreeUiState.Fail -> {
                    println("fail")
                }
                else -> {}
            }
        }

        viewModel.sendUiIntent(ThreeIntent.GetAData("a"))
    }
}