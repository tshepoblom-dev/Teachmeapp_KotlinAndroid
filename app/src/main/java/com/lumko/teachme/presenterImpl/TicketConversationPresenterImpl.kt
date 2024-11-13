package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.TicketConversationFrag
import retrofit2.Call
import retrofit2.Response

class TicketConversationPresenterImpl(private val frag: TicketConversationFrag) :
    Presenter.TicketConversationPresenter {

    override fun closeTicket(ticketId: Int) {
        ApiService.apiClient!!.closeTicket(ticketId).enqueue(object :CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    closeTicket(ticketId)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onTicketClosed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }
}