package com.data.network.model

import java.io.Serializable

data class UserBean(val id: String) : Serializable {
    lateinit var name: String
    lateinit var avatar: String
    lateinit var phone: String
    lateinit var email: String
    var createTime: Long = 0
}