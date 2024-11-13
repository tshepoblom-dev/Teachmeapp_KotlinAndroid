package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.MeetingDetailsMoreDialog
import retrofit2.Call
import retrofit2.Response

class MeetingDetailsMorePresenterImpl(private val dialog: MeetingDetailsMoreDialog) :
    Presenter.MeetingDetailsMorePresenter {

    override fun finishMeeting(meetingId: Int) {
        ApiService.apiClient!!.finishMeeting(meetingId, Any())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        finishMeeting(meetingId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingFinished(response.body()!!)
                    }
                }
            })
    }
}