package com.lumko.teachme.presenterImpl

import com.google.gson.Gson
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Login
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SignInFrag
import retrofit2.Call
import retrofit2.Response
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data

class SignInPresenterImpl(private val frag: SignInFrag) : ThirdPartyPresenterImpl(frag),
    Presenter.SignInPresenter {

    override fun login(login: Login) {
        val loginRequest = ApiService.apiClient!!.login(login)
        frag.addNetworkRequest(loginRequest)
        loginRequest.enqueue(object : CustomCallback<Data<com.lumko.teachme.model.Response>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    login(login)
                }
            }

            override fun onResponse(
                call: Call<Data<com.lumko.teachme.model.Response>>,
                response: Response<Data<com.lumko.teachme.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onSuccessfulLogin(response.body()!!)
                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }

            override fun onFailure(
                call: Call<Data<com.lumko.teachme.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }
}