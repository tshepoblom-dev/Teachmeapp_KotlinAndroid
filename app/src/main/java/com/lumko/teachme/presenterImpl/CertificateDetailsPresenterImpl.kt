package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.QuizResult
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.CertificateDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CertificateDetailsPresenterImpl(private val frag: CertificateDetailsFrag) :
    Presenter.CertificateDetailsPresenter {

    override fun getStudents() {
        val certificateStudentsReq = ApiService.apiClient!!.getCertificateStudents()
        frag.addNetworkRequest(certificateStudentsReq)
        certificateStudentsReq.enqueue(object : CustomCallback<Data<List<QuizResult>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getStudents()
                }
            }

            override fun onResponse(
                call: Call<Data<List<QuizResult>>>,
                response: Response<Data<List<QuizResult>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}