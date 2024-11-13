package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Follow
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.PricingPlansDialog
import retrofit2.Call
import retrofit2.Response

class PricingPlansPresenterImpl(private val dialog: PricingPlansDialog) :
    Presenter.PricingPlansPresenter {

    override fun purchaseWithPoints(course: Course) {
        val retryListener = RetryListener { purchaseWithPoints(course) }
        val callback = getCallback(retryListener)
        if (course.isBundle()) {
            ApiService.apiClient!!.bundlePurchaseWithPoints(course.id, Follow()).enqueue(callback)
        } else {
            ApiService.apiClient!!.purchaseWithPoints(course.id, Follow()).enqueue(callback)
        }
    }

    private fun getCallback(retryListener: RetryListener): CustomCallback<BaseResponse> {
        return object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener {
                return retryListener
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                res: Response<BaseResponse>
            ) {
                if (res.body() != null) {
                    dialog.onPurchase(res.body()!!)
                }
            }
        }
    }
}