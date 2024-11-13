package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.ReserveMeeting
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.MeetingJoinDetailsDialog
import retrofit2.Call
import retrofit2.Response

class MeetingJoinDetailsPresenterImpl(private val dialog: MeetingJoinDetailsDialog) :
    Presenter.MeetingJoinDetailsPresenter {

    override fun createJoin(reserveMeeting: ReserveMeeting) {
        ApiService.apiClient!!.createJoinForMeeting(reserveMeeting)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        createJoin(reserveMeeting)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingJoinAdded(response.body()!!)
                    }
                }

            })
    }
}