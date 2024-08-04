package com.util.base.data

import com.common.CommonUtil
import com.common.utils.SpManager

/**
 * 应用数据（全局多用户共享数据）
 */
object AppSp {
    private val sp = SpManager.of(CommonUtil.getContext(), "app")
    private const val _language = "language"
    private const val _loginUserId = "loginUserId"
    private const val _agreeUserAgreement = "agreeUserAgreement"


    fun putLanguage(language: String) {
        return sp.putString(_language, language)
    }

    // 应用语言
    fun getLanguage(): String {
        return sp.getString(_language, "")
    }

    fun putAgreeUserAgreement(agree: Boolean) {
        return sp.putBoolean(_agreeUserAgreement, agree);
    }

    // 是否授权用户协议
    fun isAgreeUserAgreement(): Boolean {
        return sp.getBoolean(_agreeUserAgreement, false)
    }


    fun putLoginUserId(userId: String) {
        return sp.putString(_loginUserId, userId);
    }

    // 登录的用户ID
    fun getLoginUserId(): String {
        return sp.getString(_loginUserId, "")
    }


    // 用户登入（注意：要先调用此方法，然后才能使用UserSp）
    fun login(userId: String) {
        putLoginUserId(userId)
        UserSp.init(userId)
    }


    // 用户登出（注意：不再使用UserSp后，最后才调用此方法）
    fun logout() {
        putLoginUserId("")
    }
}