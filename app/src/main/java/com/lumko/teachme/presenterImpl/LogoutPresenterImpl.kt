package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Follow
import com.lumko.teachme.presenter.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutPresenterImpl: Presenter.LogoutPresenter {
    override fun logout() {
        ApiService.apiClient!!.logout(Follow()).enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

            }
        })
    }
}