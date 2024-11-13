package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.RvBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.ClassListGridRvAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.Course
import com.lumko.teachme.presenterImpl.BundleCoursesPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class BundleCoursesFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!

        val presenter = BundleCoursesPresenterImpl(this)
        presenter.getBundleCourses(course.id)
    }

    fun onCoursesReceived(items: List<Course>) {
        mBinding.rvProgressBar.visibility = View.GONE

        if (items.isNotEmpty()) {
            val rv = mBinding.rv
            val layoutManager = LinearLayoutManager(requireContext())
            rv.layoutManager = layoutManager

            rv.adapter = ClassListGridRvAdapter(
                items, activity as MainActivity, layoutManager
            )
        } else {
            showEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_course, R.string.no_courses, R.string.no_courses_found)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}