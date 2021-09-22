package com.util.base.statemachine;

import android.os.Message;

import com.common.utils.log.LogUtil;
import com.common.utils.state.State;
import com.common.utils.state.StateMachine;

/**
 * 人的状态机实现类
 */
public class PersonMachineImpl extends StateMachine implements PersonMachine {
    public static final int SLEEP_EVENT = 1;
    public static final int WAKE_EVENT = 2;
    public static final int EAT_EVENT = 3;
    public static final int DRIVE_EVENT = 4;
    public static final int DREAM_EVENT = 5;

    private final SleepState sleepState = new SleepState();
    private final WakeState wakeState = new WakeState();
    private final EatState eatState = new EatState();
    private final DriveState driveState = new DriveState();
    private final DreamState dreamState = new DreamState();

    protected PersonMachineImpl() {
        init("PersonMachine");
        addState(sleepState);
        addState(wakeState);
        addState(dreamState, sleepState);
        addState(eatState, wakeState);
        addState(driveState, wakeState);
        setInitialState(wakeState);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void pause() {
        halt();
    }

    @Override
    public void resume() {
        recover();
    }

    @Override
    public void stop() {
        quit();
    }

    @Override
    public void sleep() {
        sendMessage(SLEEP_EVENT);
    }

    @Override
    public void wake() {
        sendMessage(WAKE_EVENT);
    }

    @Override
    public void eat() {
        sendMessage(EAT_EVENT);
    }

    @Override
    public void drive() {
        sendMessage(DRIVE_EVENT);
    }

    @Override
    public void dream() {
        sendMessage(DREAM_EVENT);
    }

    /**
     * 事件通知
     *
     * @param msg
     */
    @Override
    protected void onPreHandleMessage(Message msg) {
        switch (msg.what) {
            case SLEEP_EVENT:
                LogUtil.i("困啦~");
                break;
            case WAKE_EVENT:
                LogUtil.i("快起床啦~");
                break;
            case EAT_EVENT:
                LogUtil.i("吃饭啦~");
                break;
            case DRIVE_EVENT:
                LogUtil.i("出门啦~");
                break;
            case DREAM_EVENT:
                LogUtil.i("做梦啦~");
                break;
        }
    }

    /**
     * 睡觉状态的人
     */
    class SleepState implements State {

        @Override
        public void enter() {
            LogUtil.i("脱衣服...");
            LogUtil.i("睡觉中...");
        }

        @Override
        public void exit() {
            LogUtil.i("穿衣服...");
        }

        @Override
        public boolean processMessage(Message message) {
            switch (message.what) {
                case DREAM_EVENT:
                    transitionTo(dreamState);
                    break;
                case WAKE_EVENT:
                    transitionTo(wakeState);
                    break;
                case EAT_EVENT:
                    transitionTo(eatState);
                    break;
                case DRIVE_EVENT:
                    transitionTo(driveState);
                    break;

                default:
                    return NOT_HANDLED;
            }

            return HANDLED;
        }
    }

    /**
     * 做梦状态的人
     */
    class DreamState implements State {

        @Override
        public void enter() {
            LogUtil.i("开始做梦...");
            LogUtil.i("梦见美女中...");
        }

        @Override
        public void exit() {
            LogUtil.i("哎，原来是做梦...");
        }

        @Override
        public boolean processMessage(Message message) {
            switch (message.what) {
                case SLEEP_EVENT:
                    transitionTo(sleepState);
                    break;
                case WAKE_EVENT:
                    transitionTo(wakeState);
                    break;
                case EAT_EVENT:
                    transitionTo(eatState);
                    break;
                case DRIVE_EVENT:
                    transitionTo(driveState);
                    break;

                default:
                    return NOT_HANDLED;
            }

            return HANDLED;
        }
    }

    /**
     * 清醒状态的人
     */
    class WakeState implements State {

        @Override
        public void enter() {
            LogUtil.i("清醒中...");
        }

        @Override
        public void exit() {
            LogUtil.i("放松...");
        }

        @Override
        public boolean processMessage(Message message) {
            switch (message.what) {
                case SLEEP_EVENT:
                    transitionTo(sleepState);
                    break;
                case DREAM_EVENT:
                    transitionTo(dreamState);
                    break;
                case EAT_EVENT:
                    transitionTo(eatState);
                    break;
                case DRIVE_EVENT:
                    transitionTo(driveState);
                    break;

                default:
                    return NOT_HANDLED;
            }

            return HANDLED;
        }
    }

    /**
     * 吃饭状态的人
     */
    class EatState implements State {

        @Override
        public void enter() {
            LogUtil.i("洗手...");
            LogUtil.i("吃饭中...");
        }

        @Override
        public void exit() {
            LogUtil.i("吃完洗碗...");
        }

        @Override
        public boolean processMessage(Message message) {
            switch (message.what) {
                case SLEEP_EVENT:
                    transitionTo(sleepState);
                    break;
                case DREAM_EVENT:
                    transitionTo(dreamState);
                    break;
                case WAKE_EVENT:
                    transitionTo(wakeState);
                    break;
                case DRIVE_EVENT:
                    transitionTo(driveState);
                    break;

                default:
                    return NOT_HANDLED;
            }

            return HANDLED;
        }
    }

    /**
     * 开车状态的人
     */
    class DriveState implements State {

        @Override
        public void enter() {
            LogUtil.i("到车库...");
            LogUtil.i("开车中...");
        }

        @Override
        public void exit() {
            LogUtil.i("停车...");
        }

        @Override
        public boolean processMessage(Message message) {
            switch (message.what) {
                case SLEEP_EVENT:
                    transitionTo(sleepState);
                    break;
                case DREAM_EVENT:
                    transitionTo(dreamState);
                    break;
                case WAKE_EVENT:
                    transitionTo(wakeState);
                    break;
                case EAT_EVENT:
                    transitionTo(eatState);
                    break;

                default:
                    return NOT_HANDLED;
            }

            return HANDLED;
        }
    }
}
