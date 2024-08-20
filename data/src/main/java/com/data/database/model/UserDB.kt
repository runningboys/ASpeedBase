package com.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserDB(
    @PrimaryKey
    var userId: String,
    var avatar: String,
    var name: String,
    var mobile: String,
    var email: String,
    var createTime: Long,
)
