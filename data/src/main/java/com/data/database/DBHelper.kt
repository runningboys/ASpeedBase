package com.data.database

import android.annotation.SuppressLint
import com.data.database.db.AppDataBase


/**
 * 数据库辅助类
 *
 * @author LiuFeng
 * @data 2022/2/25 11:02
 */
@SuppressLint("StaticFieldLeak")
object DBHelper {
    private lateinit var userId: String


    /**
     * 初始化
     *
     * @param userId
     */
    fun init(userId: String) {
        DBHelper.userId = userId
    }


    /**
     * 获取账号ID
     *
     * @return
     */
    fun getUserId() : String {
        return userId
    }


    /**
     * 关闭数据库
     */
    fun closeDB() {
        AppDataBase.release()
    }

}