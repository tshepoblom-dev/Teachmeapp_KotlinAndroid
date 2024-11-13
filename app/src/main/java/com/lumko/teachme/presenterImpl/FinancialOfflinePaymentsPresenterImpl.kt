package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.OfflinePayment
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.FinancialOfflinePaymentsFrag
import retrofit2.Call
import retrofit2.Response

class FinancialOfflinePaymentsPresenterImpl(private val frag: FinancialOfflinePaymentsFrag) :
    Presenter.FinancialOfflinePaymentsPresenter {

    override fun getOfflinePayments() {
        val offlinePaymentsReq = ApiService.apiClient!!.getOfflinePayments()
        frag.addNetworkRequest(offlinePaymentsReq)
        offlinePaymentsReq.enqueue(object : CustomCallback<Data<List<OfflinePayment>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getOfflinePayments()
                }
            }

            override fun onResponse(
                call: Call<Data<List<OfflinePayment>>>,
                response: Response<Data<List<OfflinePayment>>>
            ) {
                if (response.body() != null) {
                    frag.onPaymentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}