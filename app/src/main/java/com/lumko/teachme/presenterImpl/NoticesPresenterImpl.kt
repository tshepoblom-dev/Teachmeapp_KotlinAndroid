package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Notice
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.NoticesFrag
import retrofit2.Call
import retrofit2.Response

class NoticesPresenterImpl(private val frag: NoticesFrag) : Presenter.NoticesPresenter {

    override fun getNotices(courseId: Int) {
        val notices = ApiService.apiClient!!.getNotices(courseId)
        frag.addNetworkRequest(notices)
        notices.enqueue(object : CustomCallback<Data<List<Notice>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getNotices(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Notice>>>,
                response: Response<Data<List<Notice>>>
            ) {
                if (response.body() != null) {
                    frag.onNoticesReceived(response.body()!!.data!!)
                }
            }
        })
    }
}