package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.PayoutRequestDialog
import retrofit2.Call
import retrofit2.Response

class PayoutRequestPresenterImpl(private val dialog: PayoutRequestDialog) :
    Presenter.PayoutRequestPresenter {

    override fun requestPayout() {
        ApiService.apiClient!!.requestPayout(Any()).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    requestPayout()
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    dialog.onPayoutSaved(response.body()!!)
                }
            }

        })
    }
}