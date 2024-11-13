package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Follow
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.ForumOptionsDialog
import retrofit2.Call
import retrofit2.Response

class ForumOptionsPresenterImpl(val dialog: ForumOptionsDialog) : Presenter.ForumOptionsPresenter {
    override fun pinForumItem(id: Int) {
        ApiService.apiClient!!.pinForumItem(id, Follow())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        pinForumItem(id)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.showResult(response.body()!!)
                    }
                }

            })
    }

    override fun pinForumItemAnswer(id: Int) {
        ApiService.apiClient!!.pinForumItemAnswer(id, Follow())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        pinForumItemAnswer(id)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.showResult(response.body()!!)
                    }
                }

            })
    }

    override fun markAnswerAsResolved(id: Int) {
        ApiService.apiClient!!.markAnswerAsResolved(id, Follow())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        markAnswerAsResolved(id)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.showResult(response.body()!!)
                    }
                }

            })
    }


}