package com.data.database.db

import androidx.room.TypeConverter
import com.common.utils.serialization.GsonUtil

/**
 * 用于room表字段的转换器
 * 备注：room转换器不支持范型，所以把具体类型写出来
 *
 * @author LiuFeng
 * @data 2020/3/3 11:25
 */
object Converters {
    @TypeConverter
    fun toListOfLong(jsonStr: String): List<Long> {
        return GsonUtil.toList(jsonStr)
    }

    @TypeConverter
    fun toJsonOfLong(data: List<Long>): String {
        return GsonUtil.toJson(data)
    }

    @TypeConverter
    fun toListOfInteger(jsonStr: String): List<Int> {
        return GsonUtil.toList(jsonStr)
    }

    @TypeConverter
    fun toJsonOfInteger(data: List<Int>): String {
        return GsonUtil.toJson(data)
    }

    @TypeConverter
    fun toListOfString(jsonStr: String): List<String> {
        return GsonUtil.toList(jsonStr)
    }

    @TypeConverter
    fun toJsonOfString(data: List<String>): String {
        return GsonUtil.toJson(data)
    }
}