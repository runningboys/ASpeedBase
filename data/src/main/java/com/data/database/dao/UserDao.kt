package com.data.database.dao

import androidx.room.*
import com.data.database.model.UserDB


@Dao
interface UserDao : BaseDao<UserDB> {

    /**
     * 查询用户表中的所有手机号
     */
    @Query("SELECT mobile FROM user")
    fun queryMobile(): List<String>


    /**
     * 删除用户
     */
    @Query("DELETE from user WHERE userId = :userId")
    fun deleteUser(userId: String)


    /**
     * 更新用户名称
     */
    @Query("UPDATE user SET name = :name WHERE userId = :userId")
    fun updateUserName(userId: String, name: String)


    /**
     * 查询所有数据
     */
    @Query("SELECT * FROM user")
    fun queryAll(): List<UserDB>

}
