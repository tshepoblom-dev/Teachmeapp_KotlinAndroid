package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.RvNestedBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.adapter.CommentRvAdapter
import com.lumko.teachme.model.Comment
import com.lumko.teachme.model.Course
import com.lumko.teachme.ui.frag.abs.EmptyState
import kotlin.math.roundToInt

class CourseDetailsCommentsFrag : Fragment(), EmptyState {

    private lateinit var mBinding: RvNestedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvNestedBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun initBottomPadding() {
        val btnsContainer =
            (parentFragment as CourseDetailsFrag).mBinding.courseDetailsPurchaseBtnsContainer
        btnsContainer.post {
            val padding =
                (btnsContainer.height + Utils.changeDpToPx(requireContext(), 20f)).roundToInt()
            mBinding.rvNestedContainer.setPadding(0, 0, 0, padding)
        }
    }

    private fun init() {
        initBottomPadding()

        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        mBinding.rvNestedProgressBar.visibility = View.GONE
        mBinding.rvNestedRv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvNestedRv.adapter = CommentRvAdapter(course.comments, childFragmentManager, false)

        initEmptyState()

        if (course.comments.isEmpty()) {
            showEmptyState()
        }
    }

    private fun initEmptyState() {
        val params = mBinding.rvNestedEmptyState.root.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.topMargin = resources.getDimension(R.dimen.margin_50).toInt()
        mBinding.rvNestedEmptyState.root.requestLayout()
    }

    fun addComment(comment: Comment) {
        hideEmptyState()
        val adatper = mBinding.rvNestedRv.adapter as CommentRvAdapter
        adatper.items.add(0, comment)
        adatper.notifyItemInserted(0)
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_comments,
            R.string.no_comments,
            R.string.no_comments_for_this_course
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvNestedEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}