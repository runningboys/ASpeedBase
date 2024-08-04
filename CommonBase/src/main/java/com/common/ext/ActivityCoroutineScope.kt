package com.common.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private var storedScope: CoroutineScope? = null

val AppCompatActivity.viewScope: CoroutineScope
    get() {
        if (storedScope != null) return storedScope as CoroutineScope

        storedScope = ActivityCoroutineScope()
        if (isFinishing || isDestroyed) {
            storedScope?.cancel()
        } else {
            lifecycle.addObserver(storedScope as ActivityCoroutineScope)
        }

        return storedScope as CoroutineScope
    }

fun AppCompatActivity?.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    this?.viewScope?.launch(context, start, block)
}


private class ActivityCoroutineScope : CoroutineScope, DefaultLifecycleObserver {
    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        coroutineContext.cancel()
        storedScope = null
    }
}