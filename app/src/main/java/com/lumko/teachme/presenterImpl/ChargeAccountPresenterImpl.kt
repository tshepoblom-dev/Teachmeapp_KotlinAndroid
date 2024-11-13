package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.PaymentRequest
import com.lumko.teachme.model.Response
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.ChargeDialog
import retrofit2.Call

class ChargeAccountPresenterImpl(private val dialog: ChargeDialog) :
    Presenter.ChargeAccountPresenter {
    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Response>>,
                    response: retrofit2.Response<Data<Response>>
                ) {
                    if (response.body() != null) {
                        dialog.onCheckout(response.body()!!)
                    }
                }

            })
    }

}