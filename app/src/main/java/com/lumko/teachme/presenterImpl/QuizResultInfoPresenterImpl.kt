package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.QuizResult
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.QuizResultInfoFrag
import retrofit2.Call
import retrofit2.Response

class QuizResultInfoPresenterImpl(private val frag: QuizResultInfoFrag) :
    Presenter.QuizResultInfoPresenter {

    override fun startQuiz(id: Int) {
        val startQuizReq = ApiService.apiClient!!.startQuiz(id)
        frag.addNetworkRequest(startQuizReq)
        startQuizReq.enqueue(object : CustomCallback<Data<QuizResult>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    startQuiz(id)
                }
            }

            override fun onResponse(
                call: Call<Data<QuizResult>>,
                response: Response<Data<QuizResult>>
            ) {
                if (response.body() != null && response.body()!!.isSuccessful) {
                    frag.onQuizStartBegin(response.body()!!)
                } else {
                    frag.cannotStartQuiz(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Data<QuizResult>>, t: Throwable) {
                super.onFailure(call, t)
                frag.cannotStartQuiz(null)
            }

        })
    }

    override fun getQuizResult(quizId: Int) {
        val quizResultReq = ApiService.apiClient!!.getQuizResult(quizId)
        frag.addNetworkRequest(quizResultReq)
        quizResultReq.enqueue(object : CustomCallback<Data<QuizResult>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getQuizResult(quizId)
                }
            }

            override fun onResponse(
                call: Call<Data<QuizResult>>,
                response: Response<Data<QuizResult>>
            ) {
                if (response.body() != null) {
                    frag.initStudentResult(response.body()!!.data!!)
                }
            }

            override fun onFailure(call: Call<Data<QuizResult>>, t: Throwable) {
                super.onFailure(call, t)
                frag.cannotStartQuiz(null)
            }

        })

    }
}