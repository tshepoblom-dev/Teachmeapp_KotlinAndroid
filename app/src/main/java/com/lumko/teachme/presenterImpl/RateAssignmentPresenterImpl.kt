package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Grade
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.RateAssignmentDialog
import retrofit2.Call
import retrofit2.Response

class RateAssignmentPresenterImpl(private val dialog: RateAssignmentDialog) :
    Presenter.RateAssignmentPresenter {

    override fun rateAssignment(assignmentId: Int, grade: Grade) {
        ApiService.apiClient!!.rateAssignment(assignmentId, grade)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        rateAssignment(assignmentId, grade)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onResponse(response.body()!!)
                    } else {
                        dialog.onRequestFailed()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onRequestFailed()
                }
            })
    }
}