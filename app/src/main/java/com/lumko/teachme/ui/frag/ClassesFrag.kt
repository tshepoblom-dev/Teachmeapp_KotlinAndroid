package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.RvBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.adapter.ClassListGridRvAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.model.view.EmptyStateData
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.ClassesPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class ClassesFrag : NetworkObserverFragment(), EmptyState {
    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.ClassesPresenter
    private var mTitle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val classes = requireArguments().getParcelableArrayList<Course>(App.COURSES)
        //val useGrid = requireArguments().getBoolean(App.USE_GRID)
        val useGrid = false
        val nestedEnabled = requireArguments().getBoolean(App.NESTED_ENABLED)

        mTitle = requireArguments().getString(App.TITLE)
        val emptyStateData = requireArguments().getParcelable<EmptyStateData>(App.EMPTY_STATE)

        if (mTitle != null) {
            val toolbarOptions = ToolbarOptions()
            toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

            (activity as MainActivity).showToolbar(toolbarOptions, mTitle!!)
        }

        if (nestedEnabled) {
            mBinding.rv.isNestedScrollingEnabled = true
        }

        if (useGrid) {
            mBinding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        }

        if (classes != null) {
            mBinding.rvProgressBar.visibility = View.GONE
            if (classes.isEmpty()) {
                showEmptyState(emptyStateData!!)
                return
            }

            if (useGrid) {
                val params = mBinding.rv.layoutParams as FrameLayout.LayoutParams
                params.marginStart = Utils.changeDpToPx(requireContext(), 16f).toInt()
                params.marginEnd = Utils.changeDpToPx(requireContext(), 16f).toInt()
                mBinding.rv.requestLayout()

                val adapter = ClassListGridRvAdapter(
                    classes, activity as MainActivity,
                    mBinding.rv.layoutManager as GridLayoutManager
                )
                mBinding.rv.adapter = adapter
            } else {
                val adapter = ClassListGridRvAdapter(classes, activity as MainActivity)
                mBinding.rv.adapter = adapter
            }
        } else {
            mBinding.rvProgressBar.visibility = View.GONE
            val map = requireArguments().getSerializable(App.FILTERS) as HashMap<String, String>
            mPresenter = ClassesPresenterImpl(
                this,
                mBinding.rv,
                map
            )

            mPresenter.getCourses()
        }
    }

    fun showEmptyStateForCourse() {
        showEmptyState(
            R.drawable.no_course,
            getString(R.string.no_courses),
            "${getString(R.string.no)} $mTitle ${getString(R.string.found)}"
        )
    }

    fun showEmptyState(emptyStateData: EmptyStateData) {
        showEmptyState(emptyStateData.img, emptyStateData.titleRes, emptyStateData.descRes)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}