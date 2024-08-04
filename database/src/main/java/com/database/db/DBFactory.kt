package com.database.db

import com.database.dao.UserDao

/**
 * 数据库工厂类
 *
 * @author LiuFeng
 * @data 2022/2/25 10:47
 */
object DBFactory {

    /**
     * 获取索引dao类
     *
     * @return
     */
    @JvmStatic
    fun getUserDao() : UserDao = AppDataBase.getInstance().getUserDao()


}