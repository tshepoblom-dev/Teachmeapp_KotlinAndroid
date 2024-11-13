package com.lumko.teachme.presenterImpl

import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.manager.adapter.EndLessClassListRvAdapter
import com.lumko.teachme.manager.adapter.EndLessLoadMoreAdapter
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Data
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.ClassesFrag
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList

class ClassesPresenterImpl(
    private val frag: ClassesFrag,
    private val rv: RecyclerView,
    private val map: HashMap<String, String>
) :
    Presenter.ClassesPresenter {

    companion object {
        private const val PAGE_COUNT = 10
    }

    private val mItems: MutableList<Course?>
    private val mAdapter: EndLessClassListRvAdapter
    private var mOffset = 0

    init {
        mItems = ArrayList()
        mAdapter = EndLessClassListRvAdapter(mItems, frag.activity as MainActivity)
        rv.adapter = mAdapter
        mAdapter.setLoadMoreListener {
            getCourses()
        }
        mAdapter.setLoading(true)
    }

    override fun getCourses() {
        rv.post {
            mItems.add(null)
            mAdapter.notifyItemInserted(mItems.size - 1)

            map["offset"] = mOffset.toString()
            map["limit"] = PAGE_COUNT.toString()
            val courses = ApiService.apiClient!!.getCourses(map)
            frag.addNetworkRequest(courses)
            courses.enqueue(object : CustomCallback<Data<List<Course>>> {
                    override fun onStateChanged(): RetryListener? {
                        return RetryListener {
                            getCourses()
                        }
                    }

                    override fun onResponse(
                        call: Call<Data<List<Course>>>,
                        response: Response<Data<List<Course>>>
                    ) {
                        if (response.body() != null) {
                            val newItems = response.body()!!.data!!

                            mOffset += PAGE_COUNT
                            val index = mItems.size - 1
                            val adapter = rv.adapter as EndLessLoadMoreAdapter<*, *>
                            mItems.removeAt(index)
                            adapter.notifyItemRemoved(index)
                            if (newItems.isNotEmpty()) {
                                mItems.addAll(newItems)
                                adapter.notifyItemRangeInserted(index, newItems.size)
                            } else {
                                adapter.isMoreDataAvailable = false
                                if (mItems.size == 0) {
                                    frag.showEmptyStateForCourse()
                                }
                            }
                            adapter.setLoading(false)
                        }
                    }
                })
        }
    }
}