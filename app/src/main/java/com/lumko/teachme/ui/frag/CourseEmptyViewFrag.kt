package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.FragCourseEmptyViewBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.model.*
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class CourseEmptyViewFrag : Fragment(), View.OnClickListener, EmptyState,
    MainActivity.OnRefreshListener {

    private lateinit var mBinding: FragCourseEmptyViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCourseEmptyViewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.cart)
        initUI()
    }

    private fun initUI() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK
        toolbarOptions.endIcon = ToolbarOptions.Icon.CART

        (activity as MainActivity).showToolbar(toolbarOptions, getString(R.string.course_details))
        showEmptyState()
    }

    fun showEmptyState() {
        if (App.loggedInUser == null) {
            mBinding.courseEmptyViewLoginBtnConatiner.visibility = View.VISIBLE
            mBinding.courseEmptyViewLoginBtn.setOnClickListener(this)
            showEmptyState(
                R.drawable.private_content,
                R.string.private_content,
                R.string.private_content_desc
            )
        } else {
            showEmptyState(
                R.drawable.pending_verification,
                R.string.pending_verification,
                R.string.pending_verification_desc
            )
        }
    }

    override fun onClick(v: View?) {
        (activity as MainActivity).goToLoginPage(this)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.courseEmptyView
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun refresh() {
        if (App.loggedInUser != null) {
            (activity as MainActivity).onBackPressed()
        }
    }
}