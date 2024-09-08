package com.util.base.ui.three

import com.common.base.mvi.IUiIntent


sealed class ThreeIntent: IUiIntent {
    data class GetAData(val value: String) : ThreeIntent()

    data class GetBData(val value: String) : ThreeIntent()

    data class GetCData(val value: String) : ThreeIntent()
}

