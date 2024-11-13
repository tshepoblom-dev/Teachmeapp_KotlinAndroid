package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SettingsFrag
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SettingsPresenterImpl(private val frag: SettingsFrag) :
    Presenter.SettingsPresenter {

    override fun uploadPhoto(path: String) {
        val file = File(path)
        val fileBody = file.asRequestBody()
        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_image", file.name, fileBody)
        ApiService.apiClient!!.changeProfileImage(filePart)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        uploadPhoto(path)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onProfileSaved(response.body()!!)
                    }
                }

            })
    }
}