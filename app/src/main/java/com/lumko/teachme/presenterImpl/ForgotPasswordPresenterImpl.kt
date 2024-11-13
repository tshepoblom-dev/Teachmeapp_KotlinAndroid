package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.ForgetPassword
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.ForgetPasswordFrag
import retrofit2.Call
import retrofit2.Response

class ForgotPasswordPresenterImpl(private val frag: ForgetPasswordFrag): Presenter.ForgotPasswordPresenter {

    override fun sendChangePasswordLink(forgetPassword: ForgetPassword) {
        val sendChangePasswordLinkReq = ApiService.apiClient!!.sendChangePasswordLink(forgetPassword)
        frag.addNetworkRequest(sendChangePasswordLinkReq)
        sendChangePasswordLinkReq.enqueue(object : CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    sendChangePasswordLink(forgetPassword)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onPasswordChanged(response.body()!!)
                }
            }

        })
    }
}