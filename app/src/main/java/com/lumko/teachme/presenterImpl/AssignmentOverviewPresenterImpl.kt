package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Assignment
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.AssignmentOverviewFrag
import retrofit2.Call
import retrofit2.Response

class AssignmentOverviewPresenterImpl(val frag: AssignmentOverviewFrag) :
    Presenter.AssignmentOverviewPresenter {

    override fun getAssignmentStudents(assignmentId: Int) {
        val assignmentStudents = ApiService.apiClient!!.getAssignmentStudents(assignmentId)
        frag.addNetworkRequest(assignmentStudents)
        assignmentStudents.enqueue(object : CustomCallback<Data<List<Assignment>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getAssignmentStudents(assignmentId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Assignment>>>,
                response: Response<Data<List<Assignment>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getAssignment(assignmentId: Int) {
        val assignment = ApiService.apiClient!!.getAssignment(assignmentId)
        frag.addNetworkRequest(assignment)
        assignment.enqueue(object : CustomCallback<Data<Assignment>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getAssignmentStudents(assignmentId)
                }
            }

            override fun onResponse(
                call: Call<Data<Assignment>>,
                response: Response<Data<Assignment>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentReceived(response.body()!!.data!!)
                }
            }
        })
    }

}