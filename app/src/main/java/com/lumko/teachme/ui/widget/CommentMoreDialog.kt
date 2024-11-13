package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogCommentMoreBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.net.observer.NetworkObserverBottomSheetDialog
import com.lumko.teachme.model.Comment
import com.lumko.teachme.ui.MainActivity

class CommentMoreDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogCommentMoreBinding
    private lateinit var mComment: Comment
    private var mItemCallback: ItemCallback<Comment>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogCommentMoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    fun setCallback(callback: ItemCallback<Comment>) {
        mItemCallback = callback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mComment = requireArguments().getParcelable(App.COMMENT)!!

        mBinding.commentMoreCancelBtn.setOnClickListener(this)
        mBinding.commentMoreReportBtn.setOnClickListener(this)
        mBinding.commentMoreReplyBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.commentMoreCancelBtn -> {
                dismiss()
            }

            R.id.commentMoreReplyBtn, R.id.commentMoreReportBtn -> {
                if (!App.isLoggedIn()) {
                    (activity as MainActivity).goToLoginPage(null)
                    return
                }

                var type = CommentDialog.Type.REPLY
                if (v.id == R.id.commentMoreReportBtn)
                    type = CommentDialog.Type.REPORT_COMMENT

                val bundle = Bundle()
                bundle.putSerializable(App.SELECTION_TYPE, type)
                bundle.putParcelable(App.COMMENT, mComment)
                bundle.putInt(App.ID, mComment.id)
                val reportDialog = CommentDialog()
//                mItemCallback?.let { reportDialog.setOnCommentSavedListener(it) }
                reportDialog.arguments = bundle
                reportDialog.show(parentFragmentManager, null)

                dismiss()
            }
        }
    }
}