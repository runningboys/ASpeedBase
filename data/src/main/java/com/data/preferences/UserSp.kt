package com.data.preferences

import com.common.utils.serialization.GsonUtil
import com.common.utils.store.SpManager
import com.data.network.model.UserBean

/**
 * 用户数据（每个用户数据独立）
 */
object UserSp {
    private lateinit var sp: SpManager
    private const val _user = "user"
    private const val _userId = "userId"
    private const val _userName = "userName"
    private const val _userAvatar = "userAvatar"


    /**
     * 初始用户SP
     */
    fun init(userId: String) {
        sp = SpManager.of(userId)
    }


    fun putUser(user: UserBean) {
        putUserId(user.id)
        putUserName(user.name)
        putUserAvatar(user.avatar)
        sp.putString(_user, GsonUtil.toJson(user))
    }

    fun getUser(): UserBean? {
        val json = sp.getString(_user, "")
        return GsonUtil.toBean(json, UserBean::class.java)
    }


   fun putUserId(userId: String) {
        sp.putString(_userId, userId)
    }

    fun getUserId(): String {
        return sp.getString(_userId, "")
    }

    fun putUserName(userName: String) {
        sp.putString(_userName, userName);
    }

    fun getUserName(): String {
        return sp.getString(_userName, "")
    }


    fun putUserAvatar(avatar: String) {
        sp.putString(_userAvatar, avatar);
    }

    fun getUserAvatar(): String {
        return sp.getString(_userAvatar, "")
    }

}