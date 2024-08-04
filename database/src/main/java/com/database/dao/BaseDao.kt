package com.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * 数据库dao基类
 *
 * @author LiuFeng
 * @data 2022/2/25 10:46
 */
interface BaseDao<T> {

    /**
     * 插入数据
     *
     * @param obj
     */
    @Insert
    fun save(obj: T)


    /**
     * 插入数据
     *
     * @param objects
     */
    @Insert
    fun save(objects: List<T>)


    /**
     * 更新数据
     *
     * @param obj
     */
    @Update
    fun update(obj: T)


    /**
     * 更新数据
     *
     * @param objects
     */
    @Update
    fun update(objects: List<T>)


    /**
     * 插入或更新数据
     *
     * @param obj
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOrUpdate(obj: T): Long


    /**
     * 插入或更新数据
     *
     * @param objects
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOrUpdate(objects: List<T>): List<Long>


    /**
     * 删除数据
     *
     * @param obj
     */
    @Delete
    fun delete(obj: T)


    /**
     * 删除数据
     *
     * @param objects
     */
    @Delete
    fun delete(objects: List<T>)
}