package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Forums
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.ForumsFrag
import retrofit2.Call
import retrofit2.Response

class ForumsPresenterImpl(private val frag: ForumsFrag) : Presenter.ForumsPresenter {

    override fun getForumQuestions(courseId: Int) {
        val courseForum = ApiService.apiClient!!.getCourseForum(courseId)
        frag.addNetworkRequest(courseForum)
        courseForum.enqueue(object : CustomCallback<Data<Forums>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getForumQuestions(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<Forums>>,
                response: Response<Data<Forums>>
            ) {
                if (response.body() != null) {
                    frag.onForumReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun searchInCourseForum(courseId: Int, s: String) {
        val searchInCourseForum = ApiService.apiClient!!.searchInCourseForum(courseId, s)
        frag.addNetworkRequest(searchInCourseForum)
        searchInCourseForum.enqueue(object : CustomCallback<Data<Forums>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getForumQuestions(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<Forums>>,
                response: Response<Data<Forums>>
            ) {
                if (response.body() != null) {
                    frag.onForumReceived(response.body()!!.data!!)
                }
            }
        })

    }
}