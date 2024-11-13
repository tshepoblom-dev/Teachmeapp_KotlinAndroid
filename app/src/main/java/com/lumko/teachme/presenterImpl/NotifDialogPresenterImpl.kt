package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.NotifDialog
import retrofit2.Call
import retrofit2.Response

class NotifDialogPresenterImpl(private val dialog: NotifDialog) : Presenter.NotifDialogPresenter {

    override fun setStatusToSeen(notifId: Int) {
        ApiService.apiClient!!.setNotifStatusToSeen(notifId)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        setStatusToSeen(notifId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null && response.body()!!.isSuccessful) {
                        dialog.onNotifSatusChange()
                    }
                }
            })
    }
}