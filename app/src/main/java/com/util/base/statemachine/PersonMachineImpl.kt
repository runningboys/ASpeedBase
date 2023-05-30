package com.util.base.statemachine

import android.os.Message
import com.common.utils.log.LogUtil
import com.common.utils.state.State
import com.common.utils.state.StateMachine

/**
 * 人的状态机实现类
 */
class PersonMachineImpl : StateMachine(), PersonMachine {
    private val sleepState = SleepState()
    private val wakeState = WakeState()
    private val eatState = EatState()
    private val driveState = DriveState()
    private val dreamState = DreamState()

    init {
        init("PersonMachine")
        addState(sleepState)
        addState(wakeState)
        addState(dreamState, sleepState)
        addState(eatState, wakeState)
        addState(driveState, wakeState)
        setInitialState(wakeState)
    }

    override fun start() {
        super.start()
    }

    override fun pause() {
        halt()
    }

    override fun resume() {
        recover()
    }

    override fun stop() {
        quit()
    }

    override fun sleep() {
        sendMessage(SLEEP_EVENT)
    }

    override fun wake() {
        sendMessage(WAKE_EVENT)
    }

    override fun eat() {
        sendMessage(EAT_EVENT)
    }

    override fun drive() {
        sendMessage(DRIVE_EVENT)
    }

    override fun dream() {
        sendMessage(DREAM_EVENT)
    }

    /**
     * 事件通知
     *
     * @param msg
     */
    override fun onPreHandleMessage(msg: Message) {
        when (msg.what) {
            SLEEP_EVENT -> LogUtil.i("困啦~")
            WAKE_EVENT -> LogUtil.i("快起床啦~")
            EAT_EVENT -> LogUtil.i("吃饭啦~")
            DRIVE_EVENT -> LogUtil.i("出门啦~")
            DREAM_EVENT -> LogUtil.i("做梦啦~")
        }
    }

    /**
     * 睡觉状态的人
     */
    internal inner class SleepState : State {
        override fun enter() {
            LogUtil.i("脱衣服...")
            LogUtil.i("睡觉中...")
        }

        override fun exit() {
            LogUtil.i("穿衣服...")
        }

        override fun processMessage(message: Message): Boolean {
            when (message.what) {
                DREAM_EVENT -> transitionTo(dreamState)
                WAKE_EVENT -> transitionTo(wakeState)
                EAT_EVENT -> transitionTo(eatState)
                DRIVE_EVENT -> transitionTo(driveState)
                else -> return NOT_HANDLED
            }
            return HANDLED
        }
    }

    /**
     * 做梦状态的人
     */
    internal inner class DreamState : State {
        override fun enter() {
            LogUtil.i("开始做梦...")
            LogUtil.i("梦见美女中...")
        }

        override fun exit() {
            LogUtil.i("哎，原来是做梦...")
        }

        override fun processMessage(message: Message): Boolean {
            when (message.what) {
                SLEEP_EVENT -> transitionTo(sleepState)
                WAKE_EVENT -> transitionTo(wakeState)
                EAT_EVENT -> transitionTo(eatState)
                DRIVE_EVENT -> transitionTo(driveState)
                else -> return NOT_HANDLED
            }
            return HANDLED
        }
    }

    /**
     * 清醒状态的人
     */
    internal inner class WakeState : State {
        override fun enter() {
            LogUtil.i("清醒中...")
        }

        override fun exit() {
            LogUtil.i("放松...")
        }

        override fun processMessage(message: Message): Boolean {
            when (message.what) {
                SLEEP_EVENT -> transitionTo(sleepState)
                DREAM_EVENT -> transitionTo(dreamState)
                EAT_EVENT -> transitionTo(eatState)
                DRIVE_EVENT -> transitionTo(driveState)
                else -> return NOT_HANDLED
            }
            return HANDLED
        }
    }

    /**
     * 吃饭状态的人
     */
    internal inner class EatState : State {
        override fun enter() {
            LogUtil.i("洗手...")
            LogUtil.i("吃饭中...")
        }

        override fun exit() {
            LogUtil.i("吃完洗碗...")
        }

        override fun processMessage(message: Message): Boolean {
            when (message.what) {
                SLEEP_EVENT -> transitionTo(sleepState)
                DREAM_EVENT -> transitionTo(dreamState)
                WAKE_EVENT -> transitionTo(wakeState)
                DRIVE_EVENT -> transitionTo(driveState)
                else -> return NOT_HANDLED
            }
            return HANDLED
        }
    }

    /**
     * 开车状态的人
     */
    internal inner class DriveState : State {
        override fun enter() {
            LogUtil.i("到车库...")
            LogUtil.i("开车中...")
        }

        override fun exit() {
            LogUtil.i("停车...")
        }

        override fun processMessage(message: Message): Boolean {
            when (message.what) {
                SLEEP_EVENT -> transitionTo(sleepState)
                DREAM_EVENT -> transitionTo(dreamState)
                WAKE_EVENT -> transitionTo(wakeState)
                EAT_EVENT -> transitionTo(eatState)
                else -> return NOT_HANDLED
            }
            return HANDLED
        }
    }

    companion object {
        const val SLEEP_EVENT = 1
        const val WAKE_EVENT = 2
        const val EAT_EVENT = 3
        const val DRIVE_EVENT = 4
        const val DREAM_EVENT = 5
    }
}