package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Coupon
import com.lumko.teachme.model.CouponValidation
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.CouponDialog
import retrofit2.Call
import retrofit2.Response

class CouponPresenterImpl(private val dialog: CouponDialog) : Presenter.CouponPresenter {

    override fun validateCoupon(coupon: Coupon) {
        ApiService.apiClient!!.validateCoupon(coupon)
            .enqueue(object : CustomCallback<Data<CouponValidation>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        validateCoupon(coupon)
                    }
                }

                override fun onResponse(
                    call: Call<Data<CouponValidation>>,
                    response: Response<Data<CouponValidation>>
                ) {
                    if (response.body() != null) {
                        dialog.onCouponValidated(response.body()!!)
                    }
                }

            })
    }


}