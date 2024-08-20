package com.data.database

import android.annotation.SuppressLint
import android.content.Context
import com.data.database.db.AppDataBase


/**
 * 数据库辅助类
 *
 * @author LiuFeng
 * @data 2022/2/25 11:02
 */
@SuppressLint("StaticFieldLeak")
object DBHelper {
    private lateinit var context: Context
    private lateinit var userId: String


    /**
     * 初始化
     *
     * @param context
     * @param userId
     */
    fun init(context: Context, userId: String) {
        DBHelper.context = context.applicationContext
        DBHelper.userId = userId
    }


    /**
     * 获取ApplicationContext
     *
     * @return
     */
    fun getContext() : Context {
        return context
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