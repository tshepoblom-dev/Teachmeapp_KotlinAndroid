package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Count
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Notif
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.NotifsFrag
import retrofit2.Call
import retrofit2.Response

class NotifPresenterImpl(private val frag: NotifsFrag) : Presenter.NotifPresenter {

    override fun getNotifs() {
        val notifsReq = ApiService.apiClient!!.getNotifs()
        frag.addNetworkRequest(notifsReq)
        notifsReq.enqueue(object :CustomCallback<Data<Count<Notif>>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getNotifs()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Notif>>>,
                response: Response<Data<Count<Notif>>>
            ) {
                if (response.body() != null) {
                    frag.onNotifsReceived(response.body()!!)
                }
            }
        })
    }
}