package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.UserChangeSettings
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SettingsGeneralFrag
import retrofit2.Call
import retrofit2.Response

class SettingsGeneralPresenterImpl(private val frag: SettingsGeneralFrag) :
    Presenter.SettingsGeneralPresenter {

    override fun changeProfileSettings(changeSettings: UserChangeSettings) {
        ApiService.apiClient!!.changeProfileSettings(changeSettings)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changeProfileSettings(changeSettings)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onSettingsChanged(response.body()!!, changeSettings)
                    }
                }

            })
    }
}