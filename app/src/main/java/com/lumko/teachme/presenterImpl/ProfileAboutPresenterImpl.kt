package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Follow
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.ProfileAboutFrag
import retrofit2.Call
import retrofit2.Response

class ProfileAboutPresenterImpl(private val frag: ProfileAboutFrag) :
    Presenter.ProfileAboutPresenter {

    override fun follow(userId: Int, follow: Follow) {
        ApiService.apiClient!!.followUser(userId, follow)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        follow(userId, follow)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onFollowed(follow)
                    }
                }
            })
    }
}