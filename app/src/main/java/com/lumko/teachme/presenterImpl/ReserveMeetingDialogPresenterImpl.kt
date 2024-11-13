package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.ReserveMeetingDialog
import retrofit2.Call
import retrofit2.Response

class ReserveMeetingDialogPresenterImpl(private val dialog: ReserveMeetingDialog) :
    Presenter.ReserveMeetingDialogPresenter {

    override fun getAvailableMeetingTimes(userId: Int, date: String) {
        val availableMeetingTimesReq = ApiService.apiClient!!.getAvailableMeetingTimes(userId, date)
        dialog.addNetworkRequest(availableMeetingTimesReq)
        availableMeetingTimesReq.enqueue(object : CustomCallback<Data<Count<Timing>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAvailableMeetingTimes(userId, date)
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Timing>>>,
                response: Response<Data<Count<Timing>>>
            ) {
                if (response.body() != null) {
                    dialog.onTimingsReceived(response.body()!!.data!!.items)
                }
            }
        })
    }
}