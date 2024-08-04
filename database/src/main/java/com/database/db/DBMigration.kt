package com.database.db

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


/**
 * Room数据库迁移处理类
 *
 * @author LiuFeng
 * @data 2022/2/25 10:54
 */
class DBMigration private constructor(startVersion: Int, endVersion: Int)
    : Migration(startVersion, endVersion) {


    /**
     * 数据迁移
     *
     * @param database
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.i(TAG, "migrate --> startVersion: " + startVersion
                + " endVersion: " + endVersion + " currentVersion: " + database.version)

        when (endVersion) {
            2 -> {
                // 举例
                //database.execSQL("ALTER TABLE Meta ADD COLUMN busItemId TEXT");
            }
            3 -> {
                //database.execSQL("···");
            }
        }
    }



    companion object {
        private const val TAG = "DBMigration"


        /**
         * 获取全部数据库迁移
         *
         * @return
         */
        @JvmStatic
        fun getAllMigrations(): Array<Migration> {
            val migrations = mutableListOf<DBMigration>()
            val newVersion = DBConfig.DB_VERSION
            for (i in 1 until newVersion) {
                migrations.add(DBMigration(i, i + 1))
            }
            return migrations.toTypedArray()
        }
    }
}