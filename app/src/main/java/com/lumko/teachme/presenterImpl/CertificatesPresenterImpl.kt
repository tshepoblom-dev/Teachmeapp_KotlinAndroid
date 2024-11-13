package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.CompletionCert
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Quiz
import com.lumko.teachme.model.QuizResult
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.CertificatesFrag
import retrofit2.Call
import retrofit2.Response

class CertificatesPresenterImpl(private val frag: CertificatesFrag) :
    Presenter.CertificatesPresenter {

    override fun getAchievementCertificates() {
        val achievementCertificatesReq = ApiService.apiClient!!.getAchievementCertificates()
        frag.addNetworkRequest(achievementCertificatesReq)
        achievementCertificatesReq
            .enqueue(object : CustomCallback<Data<List<QuizResult>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getAchievementCertificates()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<QuizResult>>>,
                    response: Response<Data<List<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onCertsReceived(response.body()!!.data!!)
                    }
                }

            })
    }

    override fun getClassCertificates() {
        val classCertificates = ApiService.apiClient!!.getClassCertificates()
        frag.addNetworkRequest(classCertificates)
        classCertificates.enqueue(object : CustomCallback<Data<Data<List<Quiz>>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAchievementCertificates()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Quiz>>>>,
                response: Response<Data<Data<List<Quiz>>>>
            ) {
                if (response.body() != null) {
                    frag.onClassCertsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

    override fun getCompletionCertificates() {
        val completionCertificates = ApiService.apiClient!!.getCompletionCertificates()
        frag.addNetworkRequest(completionCertificates)
        completionCertificates.enqueue(object : CustomCallback<Data<Data<List<CompletionCert>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getAchievementCertificates()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<CompletionCert>>>>,
                response: Response<Data<Data<List<CompletionCert>>>>
            ) {
                if (response.body() != null) {
                    frag.onCompletionCertsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

}