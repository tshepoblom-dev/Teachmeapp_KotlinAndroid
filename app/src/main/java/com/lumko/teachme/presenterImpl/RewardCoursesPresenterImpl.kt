package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.Utils.toInt
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.KeyValuePair
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.RewardCoursesFrag
import retrofit2.Call
import retrofit2.Response

class RewardCoursesPresenterImpl(private val frag: RewardCoursesFrag) :
    Presenter.RewardCoursesPresenter {

    override fun getRewardCourses(categories: List<KeyValuePair>?, options: List<KeyValuePair>?) {
        val filter = HashMap<String, String>()
        filter["reward"] = true.toInt().toString()

        if (!categories.isNullOrEmpty()){
            filter[categories[0].key] = categories[0].value
        }

        if (!options.isNullOrEmpty()) {
            for (option in options) {
                filter[option.key] = option.value
            }
        }

        ApiService.apiClient!!.getCourses(filter).enqueue(object : CustomCallback<Data<List<Course>>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getRewardCourses(categories, options)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                res: Response<Data<List<Course>>>
            ) {
                if (res.body() != null) {
                    frag.onResultReceived(res.body()!!.data!!)
                }
            }

        })
    }
}