package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.QuizAnswer
import com.lumko.teachme.model.QuizResult
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.QuizFrag
import retrofit2.Call
import retrofit2.Response

class QuizPresenterImpl(private val frag: QuizFrag) : Presenter.QuizPresenter {

    override fun storeResult(quizId: Int, answer: QuizAnswer) {
        ApiService.apiClient!!.storeQuizResult(quizId, answer)
            .enqueue(object : CustomCallback<Data<Data<QuizResult>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        storeResult(quizId, answer)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<QuizResult>>>,
                    response: Response<Data<Data<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onQuizResultSaved(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<QuizResult>>>, t: Throwable) {
                    super.onFailure(call, t)
                    frag.onRequestFailed()
                }
            })
    }
}