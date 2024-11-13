package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Review
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.CourseReviewDialog
import retrofit2.Call
import retrofit2.Response

class CourseReviewPresenterImpl(private val dialog: CourseReviewDialog) :
    Presenter.CourseReviewPresenter {

    override fun addReview(review: Review) {
        ApiService.apiClient!!.addCourseReview(review)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        addReview(review)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onReviewSaved(response.body()!!, review)
                    }
                }

            })
    }

}