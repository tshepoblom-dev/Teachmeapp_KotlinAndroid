package com.lumko.teachme.ui.frag

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragCommentDetailsBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Comment
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.CommentDetailsPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.widget.AppDialog
import com.lumko.teachme.ui.widget.CommentDialog

class CommentDetailsFrag : Fragment(), View.OnClickListener, ItemCallback<Comment> {

    private lateinit var mBinding: FragCommentDetailsBinding
    private lateinit var mPresenter: Presenter.CommentDetailsPresenter
    private lateinit var mComment: Comment
    private var mIsInstructor = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragCommentDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.comment_details)

        mComment = requireArguments().getParcelable(App.COMMENT)!!
        mIsInstructor = requireArguments().getBoolean(App.INSTRUCTOR_TYPE, false)

        if (mIsInstructor) {
            initInstructorHeader()

        } else {
            mPresenter = CommentDetailsPresenterImpl(this)

            initStudentHeader()

            mBinding.commentDetailsReplyBtn.text = getString(R.string.edit)
            mBinding.commentDetailsReportBtn.text = getString(R.string.delete)
            mBinding.commentDetailsReportBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            mBinding.commentDetailsReportBtn.strokeWidth = 0
            mBinding.commentDetailsReportBtn.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
        }

        mBinding.commentDetailsTv.text = mComment.comment
        mBinding.commentDetailsReportBtn.setOnClickListener(this)
        mBinding.commentDetailsReplyBtn.setOnClickListener(this)
    }

    private fun initInstructorHeader() {
        mBinding.commentDetailsUserNameTv.text = mComment.user!!.name
        mBinding.commentDetailsUserTypeTv.text =
            ("${getString(R.string.student)} (${getString(R.string.role)})")
        if (mComment.user!!.avatar != null) {
            Glide.with(requireContext()).load(mComment.user!!)
                .into(mBinding.commentDetailsUserImg)
        }

        mBinding.commentDetailsUserImg.visibility = View.VISIBLE
        mBinding.commentDetailsUserNameTv.visibility = View.VISIBLE
        mBinding.commentDetailsUserTypeTv.visibility = View.VISIBLE
    }

    private fun initStudentHeader() {
        mBinding.commentDetailsHeaderInfoTitleTv.text =
            getString(R.string.this_comment_is_for)
        mBinding.commentDetailsHeaderInfoDescTv.text =
            if (mComment.blog != null) mComment.blog!!.title else mComment.course!!.title

        mBinding.commentDetailsStudentHeader.visibility = View.VISIBLE
        if (mComment.course != null) {
            mBinding.commentDetailsStudentHeader.setOnClickListener(this)
        }

        val params =
            mBinding.commentDetailsTvScrollView.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = R.id.commentDetailsStudentHeader
        mBinding.commentDetailsTvScrollView.requestLayout()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.commentDetailsStudentHeader -> {
                val bundle = Bundle()
                bundle.putParcelable(App.COURSE, mComment.course)

                val frag = CourseDetailsFrag()
                frag.arguments = bundle
                (activity as MainActivity).transact(frag)
            }

            R.id.commentDetailsReportBtn, R.id.commentDetailsReplyBtn -> {
                if (!mIsInstructor && v.id == R.id.commentDetailsReportBtn) {
                    val dialog = AppDialog()
                    val bundle = Bundle()
                    bundle.putString(App.TITLE, getString(R.string.delete))
                    bundle.putString(App.TEXT, getString(R.string.delete_comment))
                    dialog.arguments = bundle
                    dialog.setOnDialogBtnsClickedListener(AppDialog.DialogType.YES_CANCEL,
                        object : AppDialog.OnDialogCreated {

                            override fun onCancel() {
                            }

                            override fun onOk() {
                                mPresenter.removeComment(mComment.id)
                            }

                        })
                    dialog.show(childFragmentManager, null)

                } else {
                    val selectionType: CommentDialog.Type = if (!mIsInstructor) {
                        CommentDialog.Type.EDIT
                    } else {
                        if (v?.id == R.id.commentDetailsReportBtn) {
                            CommentDialog.Type.REPORT_COMMENT
                        } else {
                            CommentDialog.Type.REPLY
                        }
                    }

                    val bundle = Bundle()
                    bundle.putSerializable(App.SELECTION_TYPE, selectionType)
                    bundle.putInt(App.ID, mComment.id)
                    bundle.putParcelable(App.COMMENT, mComment)

                    val dialog = CommentDialog()
//            dialog.setOnCommentSavedListener(this)
                    dialog.arguments = bundle
                    dialog.show(childFragmentManager, null)
                }

            }
        }

    }

    fun onCommentRemoved(rsp: BaseResponse) {
        if (rsp.isSuccessful) {
            activity?.onBackPressed()
        } else {
            if (context == null) return

            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                rsp.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    override fun onItem(comment: Comment, vararg args: Any) {
        mComment = comment
        mBinding.commentDetailsTv.text = mComment.comment
    }
}