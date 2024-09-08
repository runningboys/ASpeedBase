package com.data.cache.user

import com.data.network.model.UserBean
import java.util.concurrent.ConcurrentHashMap

/**
 * 用户信息缓存
 *
 * @author LiuFeng
 * @data 2021/3/30 16:04
 */
class UserCache private constructor() {
    private val cache = ConcurrentHashMap<String, UserBean>()

    /**
     * 通过id获取用户
     *
     * @param userId
     * @return
     */
    fun getUser(userId: String): UserBean? {
        return cache[userId]
    }


    /**
     * 获取所有用户
     *
     * @return
     */
    fun getAllUsers(): List<UserBean> {
        return cache.values.toList()
    }


    /**
     * 保存用户
     *
     * @param user
     */
    fun save(user: UserBean) {
        cache[user.id] = user
    }

    /**
     * 保存用户
     *
     * @param users
     */
    fun save(users: List<UserBean>) {
        for (user in users) {
            save(user)
        }
    }


    /**
     * 判空
     *
     * @return
     */
    fun isEmpty(): Boolean {
        return cache.isEmpty()
    }

    /**
     * 判断包含数据
     *
     * @param userId
     * @return
     */
    operator fun contains(userId: String): Boolean {
        return cache.containsKey(userId)
    }

    /**
     * 移除数据
     *
     * @param userId
     */
    fun remove(userId: String) {
        cache.remove(userId)
    }

    /**
     * 清空数据
     */
    fun clear() {
        cache.clear()
    }


    companion object {
        /**
         * 获取单例
         *
         * @return
         */
        val instance = UserCache()
    }
}