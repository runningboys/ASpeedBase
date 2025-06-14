package com.util.base.statemachine

/**
 * 状态机测试
 *
 * @author LiuFeng
 * @data 2021/9/22 17:34
 */
object StateMachineTest {

    fun runTest() {
        val person = PersonMachine.INSTANCE
        person.start()
        person.drive()
        person.eat()
        person.pause()
        person.dream()
        person.wake()
        person.resume()
        person.eat()
        person.sleep()
        person.stop()
        person.eat()
        person.sleep()
    }
}