package com.data.repository

import com.data.database.model.UserDB
import com.data.network.model.UserBean


fun UserBean.toUserDB(): UserDB {
    return UserDB(
        userId = id,
        name = name,
        avatar = avatar,
        mobile = phone,
        email = email,
        createTime = createTime
    )
}


fun UserDB.toUserBean(): UserBean {
    val db = this
    val bean = UserBean(userId)
    bean.name = db.name
    bean.avatar = db.avatar
    bean.phone = db.mobile
    bean.email = db.email
    bean.createTime = db.createTime
    return bean
}