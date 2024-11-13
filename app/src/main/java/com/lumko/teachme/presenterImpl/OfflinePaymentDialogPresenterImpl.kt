package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.OfflinePayment
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.OfflinePaymentDialog
import retrofit2.Call
import retrofit2.Response

class OfflinePaymentDialogPresenterImpl(private val dialog: OfflinePaymentDialog) :
    Presenter.OfflinePaymentDialogPresenter {

    override fun addOfflinePayment(offlinePayment: OfflinePayment) {
        val addOfflinePaymentsReq = ApiService.apiClient!!.addOfflinePayments(offlinePayment)
        dialog.addNetworkRequest(addOfflinePaymentsReq)
        addOfflinePaymentsReq.enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    addOfflinePayment(offlinePayment)
                }
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body() != null) {
                    dialog.onOfflinePaymentSaved(response.body()!!)
                }
            }

        })
    }
}