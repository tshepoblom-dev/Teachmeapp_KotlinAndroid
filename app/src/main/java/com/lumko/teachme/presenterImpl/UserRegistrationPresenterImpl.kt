package com.lumko.teachme.presenterImpl

import com.google.gson.Gson
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.User
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.UserRegistrationDialog
import retrofit2.Call
import retrofit2.Response

class UserRegistrationPresenterImpl(private val dialog: UserRegistrationDialog) :
    Presenter.UserRegistrationPresenter {

    override fun register(user: User) {
        val registerUserRequest = ApiService.apiClient!!.registerUser(user)
        dialog.addNetworkRequest(registerUserRequest)
        registerUserRequest.enqueue(object : CustomCallback<Data<com.lumko.teachme.model.Response>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    register(user)
                }
            }

            override fun onResponse(call: Call<Data<com.lumko.teachme.model.Response>>,
                                    response: Response<Data<com.lumko.teachme.model.Response>>) {
                if (response.body() != null) {
                    dialog.onRegistrationSaved(response.body()!!, user)

                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    dialog.onErrorOccured(error)
                }
            }
        })
    }
}