package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.ForumItemAnswer
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.ForumAnswersFrag
import retrofit2.Call
import retrofit2.Response

class ForumAnswersPresenterImpl(val frag: ForumAnswersFrag) : Presenter.ForumAnswersPresenter {

    override fun getForumQuestionAnswers(forumId: Int) {
        val forumQuestionAnswers = ApiService.apiClient!!.getForumQuestionAnswers(forumId)
        frag.addNetworkRequest(forumQuestionAnswers)
        forumQuestionAnswers.enqueue(object : CustomCallback<Data<Data<List<ForumItemAnswer>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getForumQuestionAnswers(forumId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<ForumItemAnswer>>>>,
                response: Response<Data<Data<List<ForumItemAnswer>>>>
            ) {
                if (response.body() != null) {
                    frag.onAnswersReceived(response.body()!!.data!!.data!!)
                }
            }
        })
    }
}