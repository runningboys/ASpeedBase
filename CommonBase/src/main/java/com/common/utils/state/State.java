package com.common.utils.state;

import android.os.Message;

/**
 * 描述处于此状态的行为主体
 */
public interface State {
    /**
     * 主体进入此状态
     * 备注：处理准备工作
     */
    void enter();

    /**
     * 主体退出此状态
     * 备注：处理退出工作
     */
    void exit();

    /**
     * 处理事件通知
     *
     * @param msg
     * @return
     */
    boolean processMessage(Message msg);

    /**
     * 获取状态名称
     *
     * @return
     */
    default String getName() {
        String name = getClass().getName();
        int lastDollar = name.lastIndexOf('$');
        return name.substring(lastDollar + 1);
    }
}
