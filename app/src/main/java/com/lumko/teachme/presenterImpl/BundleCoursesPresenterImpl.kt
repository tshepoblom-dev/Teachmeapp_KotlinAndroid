package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.BundleCoursesFrag
import retrofit2.Call
import retrofit2.Response

class BundleCoursesPresenterImpl(private val frag: BundleCoursesFrag) :
    Presenter.BundleCoursesPresenter {

    override fun getBundleCourses(id: Int) {
        val coursesForBundle = ApiService.apiClient!!.getCoursesForBundle(id)
        frag.addNetworkRequest(coursesForBundle)
        coursesForBundle.enqueue(object : CustomCallback<Data<Data<List<Course>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getBundleCourses(id)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Course>>>>,
                response: Response<Data<Data<List<Course>>>>
            ) {
                if (response.body() != null) {
                    frag.onCoursesReceived(response.body()!!.data!!.data!!)
                }
            }
        })
    }
}