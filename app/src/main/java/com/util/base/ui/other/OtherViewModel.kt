package com.util.base.ui.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.common.base.mvvm.BaseViewModel
import com.common.data.NetResult
import com.data.network.model.UserBean
import com.data.repository.UserRepository


class OtherViewModel: BaseViewModel() {

    fun login(phone: String, password: String): LiveData<NetResult<UserBean>> {
        return UserRepository.login(phone, password).asLiveData()
    }
}