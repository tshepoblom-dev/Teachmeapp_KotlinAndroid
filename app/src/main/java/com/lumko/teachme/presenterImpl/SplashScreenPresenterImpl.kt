package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.AppConfig
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.SplashScreenActivity
import retrofit2.Call
import retrofit2.Response

class SplashScreenPresenterImpl(private val activity: SplashScreenActivity) :
    Presenter.SplashScreenPresenter {

    override fun getAppConfig() {
        val customerSettings = ApiService.apiClient?.getAppConfig()
        activity.addNetworkRequest(customerSettings)
        customerSettings?.enqueue(object : CustomCallback<AppConfig> {
            override fun onResponse(call: Call<AppConfig>, response: Response<AppConfig>) {
                if (response.body() != null) {
                    App.appConfig = response.body()!!
                    activity.onAppConfigReceived()
                }
            }

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAppConfig()
                }
            }

            override fun onFailure(call: Call<AppConfig>, t: Throwable) {
                activity.showNoNetFrag()
            }

        })
    }
}