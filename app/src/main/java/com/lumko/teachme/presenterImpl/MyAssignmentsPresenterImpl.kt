package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Assignment
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.MyAssignmentsFrag
import retrofit2.Call
import retrofit2.Response

class MyAssignmentsPresenterImpl(private val frag: MyAssignmentsFrag) :
    Presenter.MyAssignmentsPresenter {

    override fun getMyAssignments() {
        val myAssignments = ApiService.apiClient!!.getMyAssignments()
        frag.addNetworkRequest(myAssignments)
        myAssignments.enqueue(object : CustomCallback<Data<Data<List<Assignment>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getMyAssignments()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Assignment>>>>,
                response: Response<Data<Data<List<Assignment>>>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

}