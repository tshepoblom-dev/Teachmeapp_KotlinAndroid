package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.PaymentRequest
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.ChargeAccountPaymentFrag
import retrofit2.Call
import retrofit2.Response

class ChargeAccountPaymentPresenterImpl(private val frag: ChargeAccountPaymentFrag) :
    Presenter.ChargeAccountPaymentPresenter {

    override fun requestPayment(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.requestPayment(paymentRequest)
    }

    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
    }

    override fun requestPaymentFromCharge(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.payWithCredit(paymentRequest).enqueue(object : CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    requestPaymentFromCharge(paymentRequest)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onPaymentWithCharge(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }

}