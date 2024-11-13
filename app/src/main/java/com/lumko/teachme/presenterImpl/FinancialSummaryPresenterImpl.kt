package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Count
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.FinancialSummary
import com.lumko.teachme.model.PaymentRequest
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.FinancialSummaryFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSummaryPresenterImpl(private val frag: FinancialSummaryFrag) :
    Presenter.FinancialSummaryPresenter {

    override fun getSummary() {
        val financialSummaryReq = ApiService.apiClient!!.getFinancialSummary()
        frag.addNetworkRequest(financialSummaryReq)
        financialSummaryReq.enqueue(object : CustomCallback<Data<Count<FinancialSummary>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSummary()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<FinancialSummary>>>,
                response: Response<Data<Count<FinancialSummary>>>
            ) {
                if (response.body() != null) {
                    frag.onSummariesReceived(response.body()!!.data!!.items)
                }
            }
        })
    }

    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<com.lumko.teachme.model.Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.lumko.teachme.model.Response>>,
                    response: retrofit2.Response<Data<com.lumko.teachme.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onCheckout(response.body()!!)
                    }
                }

            })
    }

}