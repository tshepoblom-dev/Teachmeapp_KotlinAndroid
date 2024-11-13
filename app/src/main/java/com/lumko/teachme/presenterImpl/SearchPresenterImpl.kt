package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SearchFrag
import retrofit2.Call
import retrofit2.Response

class SearchPresenterImpl(private val frag: SearchFrag) : Presenter.SearchPresenter {

    override fun getBestRatedCourses() {
        val map = HashMap<String, String>()
        map["offset"] = "0"
        map["limit"] = "3"
        map["sort"] = "best_rates"

        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBestRatedCourses()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onBestRatedCoursesRecevied(response.body()!!)
                }
            }
        })
    }
}