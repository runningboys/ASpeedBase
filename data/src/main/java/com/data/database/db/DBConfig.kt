package com.data.database.db

/**
 * 数据库配置信息
 *
 * @author LiuFeng
 * @data 2022/2/25 10:47
 */
object DBConfig {

    /**
     * 数据库版本号
     * 注意：数据库表结构变化后，需要手动修改数据库版本号
     */
    const val DB_VERSION = 1


    /**
     * 数据库名称
     *
     * @return
     */
    @JvmStatic
    fun getDBName(userId: String): String {
        return "userId_${userId}_db"
    }
}