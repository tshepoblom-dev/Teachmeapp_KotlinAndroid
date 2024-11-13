package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Comments
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import retrofit2.Call
import retrofit2.Response

class CommentsPresenterImpl() : Presenter.CommentsPresenter {

    override fun getComments(callback: ItemCallback<Comments>) {
        ApiService.apiClient!!.getComments().enqueue(object : CustomCallback<Data<Comments>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getComments(callback)
                }
            }

            override fun onResponse(
                call: Call<Data<Comments>>,
                response: Response<Data<Comments>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }
}