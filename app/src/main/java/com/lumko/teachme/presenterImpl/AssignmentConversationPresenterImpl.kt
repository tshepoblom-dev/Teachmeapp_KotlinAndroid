package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Assignment
import com.lumko.teachme.model.Conversation
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.AssignmentConversationFrag
import retrofit2.Call
import retrofit2.Response

class AssignmentConversationPresenterImpl(private val frag: AssignmentConversationFrag) :
    Presenter.AssignmentConversationPresenter {

    override fun getConversations(assignment: Assignment, studentId: Int?) {
        val assignmentConversations =
            ApiService.apiClient!!.getAssignmentConversations(assignment.id, studentId)
        frag.addNetworkRequest(assignmentConversations)
        assignmentConversations.enqueue(object : CustomCallback<Data<List<Conversation>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getConversations(assignment, studentId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Conversation>>>,
                response: Response<Data<List<Conversation>>>
            ) {
                if (response.body() != null) {
                    frag.onConversationsReceived(response.body()!!.data!!)
                }
            }
        })
    }

}