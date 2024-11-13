package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Category
import com.lumko.teachme.model.Count
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.widget.ProvidersFiltersDialog
import retrofit2.Call
import retrofit2.Response

class ProvidersFiltersPresenterImpl(private val dialog: ProvidersFiltersDialog) :
    Presenter.ProvidersFiltersPresenter {

    override fun getCategories() {
        val categories = ApiService.apiClient!!.getCategories()
        dialog.addNetworkRequest(categories)
        categories.enqueue(object : CustomCallback<Data<Count<Category>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCategories()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Category>>>,
                response: Response<Data<Count<Category>>>
            ) {
                if (response.body() != null)
                    dialog.onCategoriesRecevied(response.body()!!)
            }

        })
    }
}