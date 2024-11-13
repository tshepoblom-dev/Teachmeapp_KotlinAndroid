package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Count
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.SystemBankAccount
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.BanksInfoFrag
import retrofit2.Call
import retrofit2.Response

class BanksInfoPresenterImpl(private val frag: BanksInfoFrag) : Presenter.BanksInfoPresenter {

    override fun getBanksInfo() {
        val bankInfosReq = ApiService.apiClient!!.getBankInfos()
        frag.addNetworkRequest(bankInfosReq)
        bankInfosReq.enqueue(object : CustomCallback<Data<Count<SystemBankAccount>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getBanksInfo()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<SystemBankAccount>>>,
                    response: Response<Data<Count<SystemBankAccount>>>
                ) {
                    if (response.body() != null) {
                        frag.onInfosReceived(response.body()!!.data!!.items)
                    }
                }

            })
    }
}