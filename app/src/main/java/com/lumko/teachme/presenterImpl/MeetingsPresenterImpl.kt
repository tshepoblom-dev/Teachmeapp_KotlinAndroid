package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Meetings
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.MeetingsTabFrag
import retrofit2.Call
import retrofit2.Response

class MeetingsPresenterImpl(private val frag: MeetingsTabFrag) : Presenter.MeetingsPresenter {

    override fun getMeetings() {
        val meetingsReq = ApiService.apiClient!!.getMeetings()
        frag.addNetworkRequest(meetingsReq)
        meetingsReq.enqueue(object : CustomCallback<Data<Meetings>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMeetings()
                }
            }

            override fun onResponse(
                call: Call<Data<Meetings>>,
                response: Response<Data<Meetings>>
            ) {
                if (response.body() != null) {
                    frag.onMeetingsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}