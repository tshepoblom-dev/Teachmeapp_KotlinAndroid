package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Category
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.BlogCategoriesDialog
import retrofit2.Call
import retrofit2.Response

class BlogCategoriesPresenterImpl(private val dialog: BlogCategoriesDialog) : Presenter.BlogCategoriesPresenter {
    override fun getBlogCategories() {
        ApiService.apiClient!!.getBlogCategories()
            .enqueue(object : CustomCallback<Data<List<Category>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getBlogCategories()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<Category>>>,
                    response: Response<Data<List<Category>>>
                ) {
                    if (response.body() != null) {
                        dialog.onBlogCatsReceived(response.body()!!.data!!)
                    }
                }

            })
    }
}