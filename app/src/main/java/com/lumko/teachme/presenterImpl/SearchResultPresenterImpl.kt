package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.SearchObject
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.SearchResultFrag
import retrofit2.Call
import retrofit2.Response

class SearchResultPresenterImpl(private val searchFrag: SearchResultFrag) :
    Presenter.SearchResultPresenter {

    override fun search(s: String) {
        val searchCoursesAndUsers = ApiService.apiClient!!.searchCoursesAndUsers(s)
        searchFrag.addNetworkRequest(searchCoursesAndUsers)
        searchCoursesAndUsers.enqueue(object : CustomCallback<Data<SearchObject>> {
            override fun onResponse(
                call: Call<Data<SearchObject>>,
                response: Response<Data<SearchObject>>
            ) {
                if (response.body() != null) {
                    searchFrag.onSearchResultReceived(response.body()!!)
                }
            }

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    search(s)
                }
            }

        })
    }
}