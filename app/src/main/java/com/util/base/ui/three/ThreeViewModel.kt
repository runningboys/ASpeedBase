package com.util.base.ui.three

import com.common.base.mvi.IntentViewModel


class ThreeViewModel: IntentViewModel<ThreeUiState, ThreeIntent>() {
    override fun initUIState(): ThreeUiState {
        return ThreeUiState.Init
    }

    override fun handleIntent(intent: ThreeIntent) {
        when (intent) {
            is ThreeIntent.GetAData -> {
                getAData(intent.value)
            }
            is ThreeIntent.GetBData -> {
                getBData(intent.value)
            }
            is ThreeIntent.GetCData -> {
                getCData(intent.value)
            }
        }
    }


    private fun getAData(value: String) {
        sendUiState { ThreeUiState.Success("A success") }
//        sendUiState { OtherUiState.Fail("A fail") }
    }

    private fun getBData(value: String) {

    }

    private fun getCData(value: String) {

    }
}