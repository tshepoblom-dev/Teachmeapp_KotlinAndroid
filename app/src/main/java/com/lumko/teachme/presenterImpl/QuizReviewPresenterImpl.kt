package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.QuizAnswerItem
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.QuizReviewFrag
import retrofit2.Call
import retrofit2.Response

class QuizReviewPresenterImpl(private val frag: QuizReviewFrag) : Presenter.QuizReviewPresenter {

    override fun storeReviewResult(resultId: Int, review: List<QuizAnswerItem>) {
        ApiService.apiClient!!.storeReviewResult(resultId, review)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        storeReviewResult(resultId, review)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onResultStored(response.body()!!)
                    }
                }

            })
    }
}