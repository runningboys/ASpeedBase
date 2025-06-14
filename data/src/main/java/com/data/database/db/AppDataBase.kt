package com.data.database.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.common.base.BaseApp
import com.data.database.DBHelper
import com.data.database.dao.UserDao
import com.data.database.model.UserDB


/**
 * 数据库
 *
 * @author LiuFeng
 * @data 2022/2/25 14:32
 */
@Database(
    entities = [UserDB::class],
    version = DBConfig.DB_VERSION,
    exportSchema = false
)
@TypeConverters(value = [Converters::class])
abstract class AppDataBase : RoomDatabase() {

    /**
     * 用户dao类
     *
     * @return
     */
    abstract fun getUserDao() : UserDao



    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        /**
         * 获取单例
         *
         * @return
         */
        fun getInstance(): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: initInstance().also { instance = it }
            }
        }


        /**
         * 初始化实例
         */
        private fun initInstance() : AppDataBase {
            val userId = DBHelper.getUserId()
            val dbName = DBConfig.getDBName(userId)

            return Room.databaseBuilder(BaseApp.context, AppDataBase::class.java, dbName)
                // 允许主线程执行DB操作，一般不推荐
                //.allowMainThreadQueries()
                //添加数据库迁移
                .addMigrations(*DBMigration.getAllMigrations())
                // 构建实例
                .build()
        }


        /**
         * 关闭释放数据库
         */
        @Synchronized
        fun release() {
            if (instance != null) {
                instance?.close()
                instance = null
            }
        }
    }
}