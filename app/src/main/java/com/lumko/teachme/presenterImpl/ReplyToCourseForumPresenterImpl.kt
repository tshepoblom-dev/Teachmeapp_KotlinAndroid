package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.ForumItem
import com.lumko.teachme.model.Reply
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.ForumReplyDialog
import retrofit2.Call
import retrofit2.Response

class ReplyToCourseForumPresenterImpl(val dialog: ForumReplyDialog) :
    Presenter.ReplyToCourseForumPresenter {

    override fun reply(forum: ForumItem, reply: Reply) {
        if (forum.isAnswer()) {
            editReply(forum.id, reply)
        } else {
            replyToQuestion(forum.id, reply)
        }
    }

    private fun editReply(id: Int, reply: Reply) {
        ApiService.apiClient!!.editReplyToForumQuestion(id, reply)
            .enqueue(getCallback { editReply(id, reply) })
    }

    private fun replyToQuestion(id: Int, reply: Reply) {
        ApiService.apiClient!!.replyToForumQuestion(id, reply)
            .enqueue(getCallback { replyToQuestion(id, reply) })
    }

    fun getCallback(retryListener: RetryListener): CustomCallback<BaseResponse> {
        return object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener {
                return retryListener
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!)
                }
            }
        }
    }

}