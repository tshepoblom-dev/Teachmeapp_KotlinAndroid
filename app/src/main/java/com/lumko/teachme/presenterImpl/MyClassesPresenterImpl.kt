package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Count
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.MyClasses
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.MyClassesFrag
import retrofit2.Call
import retrofit2.Response

class MyClassesPresenterImpl(private val frag: MyClassesFrag) : Presenter.MyClassesPresenter {

    override fun getMyClasses() {
        val myClassesPageDataReq = ApiService.apiClient!!.getMyClassesPageData()
        frag.addNetworkRequest(myClassesPageDataReq)
        myClassesPageDataReq.enqueue(object : CustomCallback<MyClasses> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMyClasses()
                }
            }

            override fun onResponse(call: Call<MyClasses>, response: Response<MyClasses>) {
                if (response.body() != null) {
                    frag.onMyClassesReceived(response.body()!!)
                }
            }

        })
    }

    override fun getPurchased() {
        val myPurchasesReq = ApiService.apiClient!!.getMyPurchases()
        frag.addNetworkRequest(myPurchasesReq)
        myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPurchased()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Course>>>,
                response: Response<Data<Count<Course>>>
            ) {

                if (response.body() != null) {
                    frag.onPurchasedReceived(response.body()!!.data!!.items, true)
                }
            }

        })
    }

    override fun getOrganizations() {
        val myPurchasesReq = ApiService.apiClient!!.getCoursesOfOrganizations()
        frag.addNetworkRequest(myPurchasesReq)
        myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPurchased()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Course>>>,
                response: Response<Data<Count<Course>>>
            ) {

                if (response.body() != null) {
                    frag.onPurchasedReceived(response.body()!!.data!!.items, false)
                }
            }

        })
    }

}