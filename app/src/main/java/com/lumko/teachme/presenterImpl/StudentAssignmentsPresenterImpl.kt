package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.StudentAssignments
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.StudentAssignmentsFrag
import retrofit2.Call
import retrofit2.Response

class StudentAssignmentsPresenterImpl(private val frag: StudentAssignmentsFrag) :
    Presenter.StudentAssignmentsPresenter {

    override fun getStudentAssignments() {
        val studentAssignments = ApiService.apiClient!!.getStudentAssignments()
        frag.addNetworkRequest(studentAssignments)
        studentAssignments.enqueue(object : CustomCallback<Data<StudentAssignments>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getStudentAssignments()
                }
            }

            override fun onResponse(
                call: Call<Data<StudentAssignments>>,
                response: Response<Data<StudentAssignments>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}