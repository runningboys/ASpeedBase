package com.data.sp

import com.common.utils.SpManager

/**
 * 用户数据（每个用户数据独立）
 */
object UserSp {
    private lateinit var sp: SpManager
    private const val _userId = "userId"
    private const val _userName = "userName"
    private const val _userAvatar = "userAvatar"


    fun init(userId: String) {
        sp = SpManager.of(userId)
    }


   fun putUserId(userId: String) {
        return sp.putString(_userId, userId);
    }

    fun getUserId(): String {
        return sp.getString(_userId, "")
    }

    fun putUserName(userName: String) {
        return sp.putString(_userName, userName);
    }

    fun getUserName(): String {
        return sp.getString(_userName, "")
    }


    fun putUserAvatar(avatar: String) {
        return sp.putString(_userAvatar, avatar);
    }

    fun getUserAvatar(): String {
        return sp.getString(_userAvatar, "")
    }

}