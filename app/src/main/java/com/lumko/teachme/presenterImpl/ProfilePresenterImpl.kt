package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.UserProfile
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.ProfileFrag
import retrofit2.Call
import retrofit2.Response

class ProfilePresenterImpl(private val frag: ProfileFrag) : Presenter.ProfilePresenter {

    override fun getUserProfile(userId: Int) {
        val userProfileReq = ApiService.apiClient!!.getUserProfile(userId)
        frag.addNetworkRequest(userProfileReq)
        userProfileReq.enqueue(object : CustomCallback<Data<Data<UserProfile>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getUserProfile(userId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<UserProfile>>>,
                response: Response<Data<Data<UserProfile>>>
            ) {
                if (response.body() != null) {
                    frag.onUserProfileReceived(response.body()!!.data!!)
                }
            }

        })
    }
}