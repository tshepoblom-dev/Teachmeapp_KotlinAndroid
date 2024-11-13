package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.PayoutRes
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.FinancialPayoutFrag
import retrofit2.Call
import retrofit2.Response

class FinancialPayoutPresenterImpl(private val frag: FinancialPayoutFrag) :
    Presenter.FinancialPayoutPresenter {

    override fun getPayouts() {
        val payoutsReq = ApiService.apiClient!!.getPayouts()
        frag.addNetworkRequest(payoutsReq)
        payoutsReq.enqueue(object :CustomCallback<Data<PayoutRes>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPayouts()
                }
            }

            override fun onResponse(
                call: Call<Data<PayoutRes>>,
                response: Response<Data<PayoutRes>>
            ) {
                if (response.body() != null) {
                    frag.onPayoutsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}