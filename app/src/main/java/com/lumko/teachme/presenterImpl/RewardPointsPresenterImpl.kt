package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Points
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.RewardPointsFrag
import retrofit2.Call
import retrofit2.Response

class RewardPointsPresenterImpl(private val frag: RewardPointsFrag) :
    Presenter.RewardPointsPresenter {

    override fun getPoints() {
        val pointsReq = ApiService.apiClient!!.getPoints()
        frag.addNetworkRequest(pointsReq)
        pointsReq.enqueue(object : CustomCallback<Data<Points>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPoints()
                }
            }

            override fun onResponse(call: Call<Data<Points>>, res: Response<Data<Points>>) {
                if (res.body() != null) {
                    frag.onPointsReceived(res.body()!!.data!!)
                }
            }
        })
    }
}