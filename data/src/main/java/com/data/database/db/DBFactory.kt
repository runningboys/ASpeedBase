package com.data.database.db

import com.data.database.dao.UserDao

/**
 * 数据库工厂类
 *
 * @author LiuFeng
 * @data 2022/2/25 10:47
 */
object DBFactory {

    /**
     * 获取用户dao类
     */
    @JvmStatic
    fun getUserDao() : UserDao = AppDataBase.getInstance().getUserDao()


}