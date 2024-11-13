package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Ticket
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SupportFrag
import retrofit2.Call
import retrofit2.Response

class SupportPresenterImpl(private val frag: SupportFrag) : Presenter.SupportPresenter {

    override fun getTickets() {
        val supportsReq = ApiService.apiClient!!.getTickets()
        frag.addNetworkRequest(supportsReq)
        supportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getTickets()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getClassSupport() {
        val classSupportsReq = ApiService.apiClient!!.getClassSupports()
        frag.addNetworkRequest(classSupportsReq)
        classSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getMyClassSupport() {
        val myClassSupportsReq = ApiService.apiClient!!.getMyClassSupports()
        frag.addNetworkRequest(myClassSupportsReq)
        myClassSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMyClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }
}