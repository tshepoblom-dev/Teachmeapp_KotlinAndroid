package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.ChangePassword
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SettingsSecurityFrag
import retrofit2.Call
import retrofit2.Response

class SettingsSecurityPresenterImpl(private val frag: SettingsSecurityFrag) : Presenter.SettingsSecurityPresenter {

    override fun changePassword(changePassword: ChangePassword) {
        ApiService.apiClient!!.changePassword(changePassword)
            .enqueue(object : CustomCallback<Data<com.lumko.teachme.model.Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changePassword(changePassword)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.lumko.teachme.model.Response>>,
                    response: Response<Data<com.lumko.teachme.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onPasswordChanges(response.body()!!)
                    }
                }

            })
    }

}