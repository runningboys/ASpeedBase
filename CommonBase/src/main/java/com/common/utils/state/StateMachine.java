package com.common.utils.state;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.common.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 层次状态机
 */
public class StateMachine {
    // 状态机内部的4个事件：初始化、暂停、恢复、退出
    public static final int INIT = -1;
    public static final int HALT = -2;
    public static final int RECOVER = -3;
    public static final int QUIT = -4;

    // 表示消息事件能否处理
    public static final boolean HANDLED = true;
    public static final boolean NOT_HANDLED = false;

    // 用于定义内部消息的对象
    public static final Object smHandlerObj = new Object();

    private String mName;
    private boolean isQuited;
    private boolean isCompleted;
    private SmHandler smHandler;

    private State mDestState;
    private State mInitialState;
    // 状态栈存储活跃状态
    private StateInfo[] stateStack;
    private int stackTopIndex = -1;

    // 内部定义的两种状态
    private final HaltingState mHaltingState = new HaltingState();
    private final QuittingState mQuittingState = new QuittingState();

    // 存储状态树
    private final Map<State, StateInfo> stateMap = new HashMap<>();
    // 存储延迟消息
    private final List<Message> deferredMessages = new ArrayList<>();


    /**
     * 初始化状态机
     * 备注：状态机初始化后才能启动
     *
     * @param name
     */
    public void init(String name) {
        if (smHandler == null) {
            HandlerThread thread = new HandlerThread(name);
            thread.start();
            init(name, thread.getLooper());
        }
    }

    /**
     * 初始化状态机
     *
     * @param name
     * @param handler
     */
    public void init(String name, Handler handler) {
        init(name, handler.getLooper());
    }


    /**
     * 初始化状态机
     *
     * @param name
     * @param looper
     */
    public void init(String name, Looper looper) {
        if (smHandler == null) {
            mName = name;
            isQuited = false;
            smHandler = new SmHandler(looper, this);
            addState(mHaltingState, null);
            addState(mQuittingState, null);
        }
    }

    /**
     * 添加树的根状态
     * 备注：可以有多个根状态
     *
     * @param state
     */
    public final void addState(State state) {
        addState(state, null);
    }

    /**
     * 添加子状态和其父状态
     * 注意：同一个孩子不允许有不同的父亲
     *
     * @param state
     * @param parent
     * @return
     */
    public final StateInfo addState(State state, State parent) {
        // 先添加父节点
        StateInfo parentInfo = stateMap.get(parent);
        if (parent != null && parentInfo == null) {
            parentInfo = addState(parent, null);
        }

        // 再添加子节点
        StateInfo childInfo = stateMap.get(state);
        if (childInfo == null) {
            childInfo = new StateInfo();
            childInfo.state = state;
            childInfo.parentStateInfo = parentInfo;
            childInfo.active = false;
            stateMap.put(state, childInfo);
        } else {
            if (childInfo.parentStateInfo != null && childInfo.parentStateInfo != parentInfo) {
                throw new RuntimeException("child have different parent nodes");
            }
        }

        return childInfo;
    }

    /**
     * 移除状态
     * 备注：非活跃的、且没有子状态，才能移除！
     *
     * @param state
     */
    public final void removeState(State state) {
        StateInfo stateInfo = stateMap.get(state);
        if (stateInfo == null || stateInfo.active) {
            return;
        }

        // 遍历当前状态是否有子状态
        boolean hasChild = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            hasChild = stateMap.values().stream().anyMatch(si -> si.parentStateInfo == stateInfo);
        } else {
            for (StateInfo info : stateMap.values()) {
                if (info.parentStateInfo == stateInfo) {
                    hasChild = true;
                    break;
                }
            }
        }

        if (!hasChild) {
            stateMap.remove(state);
        }
    }

    /**
     * 设置状态机启动后到达的节点状态为初始状态
     *
     * @param state
     */
    public final void setInitialState(State state) {
        mInitialState = state;
    }

    /**
     * 获取当前状态
     *
     * @return
     */
    public final State getCurrentState() {
        return stateStack[stackTopIndex].state;
    }

    /**
     * 转换到目标状态
     *
     * @param destState
     */
    public final void transitionTo(State destState) {
        this.mDestState = destState;
    }

    /**
     * 启动状态机
     */
    public void start() {
        if (smHandler == null) {
            return;
        }

        // 求整颗状态树的深度
        int maxDepth = getTreeMaxDepth();
        // 创建存储活跃状态栈
        stateStack = new StateInfo[maxDepth];
        stackTopIndex = - 1;

        // 找出开始状态到根节点的状态路径
        List<StateInfo> sToRootPaths = getDestToRootPaths(mInitialState);

        // 从根到开始节点依次存入栈
        reverseAddToStack(sToRootPaths);

        // 将初始化消息放入队首，最先处理
        sendMessageAtFrontOfQueue(INIT, smHandlerObj);
    }

    /**
     * 求整颗状态树的深度
     *
     * @return
     */
    private int getTreeMaxDepth() {
        int maxDepth = 0;
        Map<StateInfo, Integer> depthMap = new HashMap<>();
        for (StateInfo info : stateMap.values()) {
            // 计算当前状态在树中的深度
            int depth = 0;
            StateInfo cur = info;
            while (cur != null) {
                Integer value = depthMap.get(cur);
                if (value != null) {
                    depth += value;
                    break;
                }

                depth++;
                cur = cur.parentStateInfo;
            }

            // 保存当前状态和所有父级状态的深度（复用已计算状态，时间复杂度为O(n)）
            cur = info;
            for (int i = depth; i > 0; i--) {
                if (depthMap.containsKey(cur)) {
                    break;
                }

                depthMap.put(cur, i);
                cur = cur.parentStateInfo;
            }

            maxDepth = Math.max(depth, maxDepth);
        }
        return maxDepth;
    }

    /**
     * 暂停状态机
     * 备注：暂停后不处理事件通知
     */
    public final void halt() {
        sendMessage(HALT, smHandlerObj);
    }

    /**
     * 立即暂停状态机
     * 备注：不等前面的事件处理完，先处理暂停
     */
    public final void haltNow() {
        sendMessageAtFrontOfQueue(HALT, smHandlerObj);
    }

    /**
     * 恢复状态机
     */
    public final void recover() {
        sendMessage(RECOVER, smHandlerObj);
    }

    /**
     * 退出状态机
     */
    public final void quit() {
        sendMessage(QUIT, smHandlerObj);
    }

    /**
     * 立即退出状态机
     */
    public final void quitNow() {
        sendMessageAtFrontOfQueue(QUIT, smHandlerObj);
    }

    /**
     * 发送事件消息
     *
     * @param what
     */
    public void sendMessage(int what) {
        sendMessage(what, null);
    }

    /**
     * 发送事件消息
     *
     * @param what
     * @param obj
     */
    public void sendMessage(int what, Object obj) {
        if (smHandler == null) {
            return;
        }

        smHandler.sendMessage(smHandler.obtainMessage(what, obj));
    }

    /**
     * 发送事件消息到队列头部
     *
     * @param what
     * @param obj
     */
    public void sendMessageAtFrontOfQueue(int what, Object obj) {
        if (smHandler == null) {
            return;
        }

        smHandler.sendMessageAtFrontOfQueue(smHandler.obtainMessage(what, obj));
    }

    /**
     * 延迟消息
     *
     * @param msg
     */
    public final void deferMessage(Message msg) {
        Message newMsg = Message.obtain(smHandler);
        newMsg.copyFrom(msg);
        deferredMessages.add(newMsg);
    }

    /**
     * 状态机名称
     *
     * @return
     */
    protected String getName() {
        return mName;
    }

    /**
     * 状态机是否已退出
     *
     * @return
     */
    protected boolean isQuited() {
        return isQuited;
    }

    /**
     * 状态机是否已启动完成
     *
     * @return
     */
    protected boolean isCompleted() {
        return isCompleted;
    }

    /**
     * 设置已完成
     *
     * @param completed
     */
    void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * 非初始化和退出的消息，处理前通知回调，按需重写
     *
     * @param msg
     */
    protected void onPreHandleMessage(Message msg) {}

    /**
     * 非初始化和退出的消息，处理后通知回调，按需重写
     *
     * @param msg
     */
    protected void onPostHandleMessage(Message msg) {}

    /**
     * 无法处理的消息，按需重写
     *
     * @param msg
     */
    protected void unhandledMessage(Message msg) {}

    /**
     * 暂停状态下收到的消息，按需重写
     *
     * @param msg
     */
    protected void haltedProcessMessage(Message msg) {}

    /**
     * 暂停中，按需重写
     */
    protected void onHalting() {}

    /**
     * 退出中，按需重写
     */
    protected void onQuitting() {}

    /**
     * 初始化消息
     *
     * @param msg
     * @return
     */
    protected boolean isInit(Message msg) {
        return (msg.what == INIT) && (msg.obj == smHandlerObj);
    }

    /**
     * 暂停消息
     *
     * @param msg
     * @return
     */
    protected boolean isHalt(Message msg) {
        return (msg.what == HALT) && (msg.obj == smHandlerObj);
    }

    /**
     * 恢复消息
     *
     * @param msg
     * @return
     */
    protected boolean isRecover(Message msg) {
        return (msg.what == RECOVER) && (msg.obj == smHandlerObj);
    }

    /**
     * 退出消息
     *
     * @param msg
     * @return
     */
    protected boolean isQuit(Message msg) {
        return (msg.what == QUIT) && (msg.obj == smHandlerObj);
    }

    /**
     * 处理事件消息
     *
     * @param msg
     */
    final void processMsg(Message msg) {
        StateInfo curInfo = stateStack[stackTopIndex];
        if (isHalt(msg)) {
            transitionTo(mHaltingState);
            return;
        }

        if (isQuit(msg)) {
            transitionTo(mQuittingState);
            return;
        }

        while (curInfo != null && !curInfo.state.processMessage(msg)) {
            curInfo = curInfo.parentStateInfo;
        }

        if (curInfo == null) {
            unhandledMessage(msg);
        }
    }

    /**
     * 执行状态转换
     */
    final void performTransitions() {
        State destState = mDestState;
        if (destState != null) {
            while (true) {
                List<StateInfo> destToActivePaths = getDestToActivePaths(destState);
                int size = destToActivePaths.size();
                if (size > 0) {
                    StateInfo commonParentInfo = destToActivePaths.get(size - 1).parentStateInfo;
                    invokeExitMethods(commonParentInfo);
                    int startingIndex = reverseAddToStack(destToActivePaths);
                    invokeEnterMethods(startingIndex);
                    moveDeferredMessageAtFrontOfQueue();

                    // 在退出和进入状态时，可能修改了目标状态，所以这里用while循环保证执行完成最终一致
                    if (destState != mDestState) {
                        destState = mDestState;
                    } else {
                        break;
                    }
                }
            }

            // 目标执行后置空
            mDestState = null;
        }

        if (destState == mQuittingState) {
            onQuitting();
            cleanupAfterQuitting();
        } else if (destState == mHaltingState) {
            onHalting();
        }
    }


    /**
     * 获取目标到根状态的路径
     *
     * @param destState
     * @return
     */
    private List<StateInfo> getDestToRootPaths(State destState) {
        List<StateInfo> paths = new ArrayList<>();
        StateInfo cur = stateMap.get(destState);
        while (cur != null) {
            paths.add(cur);
            cur = cur.parentStateInfo;
        }
        return paths;
    }

    /**
     * 获取目标到活跃状态的路径（不包含活跃的）
     *
     * @param destState
     * @return
     */
    private List<StateInfo> getDestToActivePaths(State destState) {
        List<StateInfo> paths = new ArrayList<>();
        StateInfo cur = stateMap.get(destState);
        do {
            paths.add(cur);
            cur = cur.parentStateInfo;
        } while (cur != null && !cur.active);

        return paths;
    }

    /**
     * 反转路径，再依次入栈
     *
     * @param paths
     * @return
     */
    private int reverseAddToStack(List<StateInfo> paths) {
        int startingIndex = stackTopIndex + 1;
        for (int i = paths.size() - 1; i >= 0; i--) {
            stateStack[++stackTopIndex] = paths.get(i);
        }

        return startingIndex;
    }

    /**
     * 调用进入状态
     *
     * @param startIndex
     */
    void invokeEnterMethods(int startIndex) {
        for (int i = startIndex; i <= stackTopIndex; i++) {
            StateInfo info = stateStack[i];
            info.state.enter();
            info.active = true;
        }
    }

    /**
     * 调用退出状态
     *
     * @param stateInfo
     */
    void invokeExitMethods(StateInfo stateInfo) {
        while (stackTopIndex >= 0 && stateStack[stackTopIndex] != stateInfo) {
            StateInfo curInfo = stateStack[stackTopIndex];
            curInfo.state.exit();
            curInfo.active = false;
            stackTopIndex--;
        }
    }

    /**
     * 将延迟消息移动到队列头部
     */
    private void moveDeferredMessageAtFrontOfQueue() {
        for (int i = deferredMessages.size() - 1; i >= 0; i--) {
            Message curMsg = deferredMessages.get(i);
            smHandler.sendMessageAtFrontOfQueue(curMsg);
        }
        deferredMessages.clear();
    }

    /**
     * 退出清空
     */
    private void cleanupAfterQuitting() {
        if (smHandler != null) {
            smHandler.getLooper().quit();
        }

        smHandler = null;
        stateStack = null;
        stateMap.clear();
        mInitialState = null;
        mDestState = null;
        deferredMessages.clear();
        stackTopIndex = -1;
        isCompleted = false;
        isQuited = true;
    }


    /**
     * 退出状态
     */
    static class QuittingState implements State {
        @Override
        public void enter() {
            LogUtil.i("退出...");
        }

        @Override
        public void exit() {
        }

        @Override
        public boolean processMessage(Message msg) {
            return NOT_HANDLED;
        }
    }


    /**
     * 暂停状态
     */
    class HaltingState implements State {
        @Override
        public void enter() {
            LogUtil.i("进入暂停...");
        }

        @Override
        public void exit() {
            LogUtil.i("退出暂停...");
        }

        @Override
        public boolean processMessage(Message msg) {
            if (isCompleted && isRecover(msg)) {
                transitionTo(mInitialState);
            } else {
                haltedProcessMessage(msg);
            }
            return true;
        }
    }


    /**
     * 状态信息类（指向父状态的单链表）
     */
    static class StateInfo {
        /**
         * 当前状态
         */
        State state;

        /**
         * 父状态信息
         */
        StateInfo parentStateInfo;

        /**
         * 当前状态是否活跃
         */
        boolean active;
    }


    /**
     * 状态机的消息收发处理者
     */
    public static class SmHandler extends Handler {
        private final StateMachine machine;

        public SmHandler(Looper looper, StateMachine sm) {
            super(looper);
            this.machine = sm;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            // 状态机退出后，不处理任何事件
            if (machine.isQuited()) {
                return;
            }

            // 非初始化和退出的消息，处理前通知回调
            if (msg.what != StateMachine.INIT && msg.what != StateMachine.QUIT) {
                machine.onPreHandleMessage(msg);
            }

            // 执行状态机初始化
            if (!machine.isCompleted() && machine.isInit(msg)) {
                machine.setCompleted(true);
                machine.invokeEnterMethods(0);
            }
            // 处理状态机事件通知
            else if (machine.isCompleted() || msg.what == StateMachine.QUIT) {
                machine.processMsg(msg);
            } else {
                throw new RuntimeException(" StateMachine is not start");
            }

            // 执行状态转移
            machine.performTransitions();

            // 非初始化和退出的消息，处理后通知回调
            if (msg.what != StateMachine.INIT && msg.what != StateMachine.QUIT) {
                machine.onPostHandleMessage(msg);
            }
        }
    }

}
