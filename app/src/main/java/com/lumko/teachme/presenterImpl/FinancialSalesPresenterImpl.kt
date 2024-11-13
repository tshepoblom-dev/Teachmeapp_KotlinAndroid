package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.SalesRes
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.FinancialSalesFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSalesPresenterImpl(private val frag: FinancialSalesFrag) :
    Presenter.FinancialSalesPresenter {

    override fun getSales() {
        val salesReq = ApiService.apiClient!!.getSales()
        frag.addNetworkRequest(salesReq)
        salesReq.enqueue(object : CustomCallback<Data<SalesRes>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSales()
                }
            }

            override fun onResponse(
                call: Call<Data<SalesRes>>,
                response: Response<Data<SalesRes>>
            ) {
                if (response.body() != null) {
                    frag.onSalesReceived(response.body()!!.data!!)
                }
            }

        })
    }
}