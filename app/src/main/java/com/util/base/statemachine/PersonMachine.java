package com.util.base.statemachine;

/**
 * 基于人的行为状态机
 */
public interface PersonMachine {
    PersonMachine INSTANCE = new PersonMachineImpl();

    void start();

    void pause();

    void resume();

    void stop();

    void sleep();

    void wake();

    void eat();

    void drive();

    void dream();
}
