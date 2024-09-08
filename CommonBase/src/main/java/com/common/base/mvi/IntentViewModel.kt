package com.common.base.mvi

import androidx.lifecycle.viewModelScope
import com.common.base.mvvm.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


abstract class IntentViewModel<UiState : IUIState, UiIntent : IUiIntent> : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow(initUIState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow

    private val intentChannel: Channel<UiIntent> = Channel()


    protected abstract fun initUIState(): UiState
    protected abstract fun handleIntent(intent: UiIntent)

    init {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                handleIntent(it)
            }
        }
    }


    fun observeUiState(observer: (UiState) -> Unit) = launch {
        _uiStateFlow.collect {
            observer(it)
        }
    }

    fun sendUiIntent(uiIntent: UiIntent) {
        viewModelScope.launch {
            intentChannel.send(uiIntent)
        }
    }

    protected fun sendUiState(copy: UiState.() -> UiState) {
        _uiStateFlow.update { copy(_uiStateFlow.value) }
    }

}