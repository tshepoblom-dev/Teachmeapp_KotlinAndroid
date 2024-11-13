package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Message
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.NewMessageDialog
import retrofit2.Call
import retrofit2.Response

class NewMessagePresenterImpl(private val dialog: NewMessageDialog) :
    Presenter.NewMessagePresenter {


    override fun addMessage(userId: Int, message: Message) {
        ApiService.apiClient!!.addNewMessage(userId, message)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        addMessage(userId, message)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMessageAdded(response.body()!!)
                    }
                }

            })
    }
}