package com.util.base.statemachine

/**
 * 基于人的行为状态机
 */
interface PersonMachine {
    fun start()
    fun pause()
    fun resume()
    fun stop()
    fun sleep()
    fun wake()
    fun eat()
    fun drive()
    fun dream()

    companion object {
        @JvmField
        val INSTANCE: PersonMachine = PersonMachineImpl()
    }
}