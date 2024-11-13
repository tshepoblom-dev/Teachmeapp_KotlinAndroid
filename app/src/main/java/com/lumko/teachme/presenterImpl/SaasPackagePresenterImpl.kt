package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.SaasPackage
import com.lumko.teachme.model.SaasPackageItem
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SaasPackageFrag
import retrofit2.Call
import retrofit2.Response

class SaasPackagePresenterImpl(private val frag: SaasPackageFrag) : Presenter.SaasPackagePresenter {

    override fun getSaasPackages() {
        ApiService.apiClient!!.getSaasPackages()
            .enqueue(object : CustomCallback<Data<SaasPackage>> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        getSaasPackages()
                    }
                }

                override fun onResponse(
                    call: Call<Data<SaasPackage>>,
                    res: Response<Data<SaasPackage>>
                ) {
                    if (res.body() != null) {
                        frag.onSaasPackageReceived(res.body()!!.data!!)
                    }
                }

            })
    }

    override fun checkoutSubscription(saasPackageItem: SaasPackageItem) {
        ApiService.apiClient!!.checkoutSaasPackage(saasPackageItem)
            .enqueue(object : CustomCallback<Data<com.lumko.teachme.model.Response>> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        checkoutSubscription(saasPackageItem)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.lumko.teachme.model.Response>>,
                    res: Response<Data<com.lumko.teachme.model.Response>>
                ) {
                    if (res.body() != null) {
                        frag.onCheckout(res.body()!!)
                    }
                }

            })
    }
}