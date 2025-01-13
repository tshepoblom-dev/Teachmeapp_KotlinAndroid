package com.lumko.teachme.presenterImpl

import com.google.gson.Gson
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SignUpFrag
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response

class SignUpPresenterImpl(private val frag: SignUpFrag) : ThirdPartyPresenterImpl(frag),
    Presenter.SignUpPresenter {

    override fun signUp(signUp: EmailSignUp, multipart: MutableList<MultipartBody.Part>) {
        //convert data to json
        val gson = Gson()
        val signUpJson = gson.toJson(signUp) // Convert the object to JSON
        val signUpPart = signUpJson.toRequestBody("application/json".toMediaTypeOrNull())

        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUpPart, multipart)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(emailSignUp = signUp))
    }
/*
    override fun signUp(signUp: EmailSignUp, vararg files: MultipartBody.Part) {
        //convert data to json
        val gson = Gson()
        val signUpJson = gson.toJson(signUp) // Convert the object to JSON
        val signUpPart = signUpJson.toRequestBody("application/json".toMediaTypeOrNull())

        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUpPart, *files)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(emailSignUp = signUp))
    }*/
    override fun signUp(signUp: MobileSignUp, multipart: MutableList<MultipartBody.Part>) {
        //convert data to json
        val gson = Gson()
        val signUpJson = gson.toJson(signUp) // Convert the object to JSON
        val signUpPart = signUpJson.toRequestBody("application/json".toMediaTypeOrNull())

        //val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp, multipart)
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUpPart, multipart)
       // val signUpRequest = ApiService.apiClient!!.signUpMethod(signUpPart)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(mobileSignUp = signUp))
    }

    //override fun signUpFiles(vararg multipart: MultipartBody.Part) {
        //ApiService.apiClient!!.signUpMethodFiles(multipart.toList())
    override fun signUpFiles(userId: Int, multipart: List<MultipartBody.Part>) {
        val usersId = userId.toString()
           // .toRequestBody("text/plain".toMediaTypeOrNull()) // Replace "12345" with the actual user ID
        ApiService.apiClient!!.signUpMethodFiles(usersId, multipart)
            .enqueue(object: CustomCallback<BaseResponse>{
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        //signUpFiles(*multipart)
                        signUpFiles(userId, multipart)
                    }
                }
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                     //   frag.onFilesSaved(response.body()!!)
                    }
                }

            })
    }
    override fun signUp(signUp: EmailSignUp) {
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(emailSignUp = signUp))
    }

    override fun signUp(signUp: MobileSignUp) {
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(mobileSignUp = signUp))
    }
    private fun getCallback(
        emailSignUp: EmailSignUp? = null,
        mobileSignUp: MobileSignUp? = null
    ): CustomCallback<Data<User>> {
        return object : CustomCallback<Data<User>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    if (emailSignUp != null) {
                        signUp(emailSignUp)
                    } else {
                        signUp(mobileSignUp!!)
                    }
                }
            }

            override fun onResponse(call: Call<Data<User>>, response: Response<Data<User>>) {
                if (response.body() != null) {
                    if (emailSignUp != null) {
                        frag.onUserBasicsSaved(response.body()!!, emailSignUp = emailSignUp)
                    } else {
                        frag.onUserBasicsSaved(response.body()!!, mobileSignUp = mobileSignUp)
                    }
                    var userID = response.body()!!.data!!.userId
                    if(userID != null)
                        frag.uploadFiles(userID)
                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }
        }
    }
}