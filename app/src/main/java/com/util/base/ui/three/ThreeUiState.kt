package com.util.base.ui.three

import com.common.base.mvi.IUIState


sealed class ThreeUiState: IUIState {
    object Init : ThreeUiState()
    data class Success(val data: String): ThreeUiState()
    data class Fail(val msg: String?): ThreeUiState()
}
