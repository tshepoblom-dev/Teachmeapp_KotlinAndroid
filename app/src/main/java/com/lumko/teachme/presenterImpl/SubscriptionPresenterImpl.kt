package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Subscription
import com.lumko.teachme.model.SubscriptionItem
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SubscriptionFrag
import retrofit2.Call
import retrofit2.Response

class SubscriptionPresenterImpl(private val frag: SubscriptionFrag) :
    Presenter.SubscriptionPresenter {

    override fun getSubscriptions() {
        val subscriptionsReq = ApiService.apiClient!!.getSubscriptions()
        frag.addNetworkRequest(subscriptionsReq)
        subscriptionsReq.enqueue(object : CustomCallback<Data<Subscription>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSubscriptions()
                }
            }

            override fun onResponse(
                call: Call<Data<Subscription>>,
                response: Response<Data<Subscription>>
            ) {
                if (response.body() != null) {
                    frag.onSubscriptionsReceived(response.body()!!.data!!)
                }
            }

        })
    }

    override fun checkoutSubscription(subscriptionItem: SubscriptionItem) {
        val checkoutSubscriptionReq = ApiService.apiClient!!.checkoutSubscription(subscriptionItem)
        frag.addNetworkRequest(checkoutSubscriptionReq)
        checkoutSubscriptionReq.enqueue(object :
            CustomCallback<Data<com.lumko.teachme.model.Response>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    checkoutSubscription(subscriptionItem)
                }
            }

            override fun onResponse(
                call: Call<Data<com.lumko.teachme.model.Response>>,
                response: Response<Data<com.lumko.teachme.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onCheckout(response.body()!!)
                }
            }

            override fun onFailure(
                call: Call<Data<com.lumko.teachme.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        }

        )
    }
}