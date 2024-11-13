package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Blog
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.BlogsFrag
import retrofit2.Call
import retrofit2.Response

class BlogPresenterImpl(private val frag: BlogsFrag) : Presenter.BlogPresenter {

    override fun getBlogs() {
        val blogs = ApiService.apiClient!!.getBlogs()
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBlogs()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getBlogs(catId: Int) {
        val blogs = ApiService.apiClient!!.getBlogs(catId)
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBlogs(catId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }
}