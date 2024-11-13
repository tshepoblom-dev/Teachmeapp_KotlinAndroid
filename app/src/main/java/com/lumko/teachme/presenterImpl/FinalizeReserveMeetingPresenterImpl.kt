package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.ReserveTimeMeeting
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.FinalizeReserveMeetingDialog
import retrofit2.Call
import retrofit2.Response

class FinalizeReserveMeetingPresenterImpl(private val dialog: FinalizeReserveMeetingDialog) :
    Presenter.FinalizeReserveMeetingPresenter {

    override fun reserveMeeting(reserveMeeting: ReserveTimeMeeting) {
        ApiService.apiClient!!.reserveMeeting(reserveMeeting)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        reserveMeeting(reserveMeeting)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingReserved(response.body()!!)
                    } else {
                        dialog.onFailed()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onFailed()
                }
            })
    }
}