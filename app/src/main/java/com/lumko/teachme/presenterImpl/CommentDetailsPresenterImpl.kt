package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.CommentDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CommentDetailsPresenterImpl(private val frag: CommentDetailsFrag) :
    Presenter.CommentDetailsPresenter {

    override fun removeComment(commentId: Int) {
        ApiService.apiClient!!.removeComment(commentId)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        removeComment(commentId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onCommentRemoved(response.body()!!)
                    }
                }
            })
    }
}