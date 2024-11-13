package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.ForumItem
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.ForumQuestionDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class ForumQuestionPresenterImpl(val dialog: ForumQuestionDialog) :
    Presenter.ForumQuestionPresenter {

    fun getCallback(retryListener: RetryListener): CustomCallback<BaseResponse> {
        val callback = object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener {
                return retryListener
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    dialog.onResponse(response.body()!!)
                }
            }
        }
        return callback
    }

    override fun sendQuestion(courseId: Int, forumItem: ForumItem, file: File?) {
        var filePart: MultipartBody.Part? = null

        if (file != null) {
            val fileBody = file.asRequestBody()
            filePart = MultipartBody.Part.createFormData("attachment", file.name, fileBody)
        }

        ApiService.apiClient!!.postForumQuestion(
            courseId,
            MultipartBody.Part.createFormData("title", forumItem.title),
            MultipartBody.Part.createFormData("description", forumItem.description),
            filePart
        ).enqueue(getCallback { sendQuestion(courseId, forumItem, file) })
    }

    override fun editQuestion(forumItem: ForumItem, file: File?) {
        if (file == null) {
            ApiService.apiClient!!.editForumQuestion(
                forumItem.id,
                forumItem
            ).enqueue(getCallback { editQuestion(forumItem, file) })
        } else {
            val fileBody = file.asRequestBody()
            val filePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("attachment", file.name, fileBody)

            ApiService.apiClient!!.editForumQuestion(
                forumItem.id,
                MultipartBody.Part.createFormData("title", forumItem.title),
                MultipartBody.Part.createFormData("description", forumItem.description),
                filePart,
            ).enqueue(getCallback { editQuestion(forumItem, file) })
        }
    }
}