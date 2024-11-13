package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogRateAssignmentBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.net.observer.NetworkObserverBottomSheetDialog
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.RateAssignmentPresenterImpl

class RateAssignmentDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogRateAssignmentBinding
    private lateinit var mPresenter: Presenter.RateAssignmentPresenter
    private var mAssignmentId = 0
    private var mCallback: ItemCallback<Any>? = null
    private lateinit var mLoadingDialog: LoadingDialog

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            mBinding.rateAssignmentSendBtn.isEnabled =
                mBinding.rateAssignmentGradeEdtx.text.toString().isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogRateAssignmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mAssignmentId = requireArguments().getInt(App.ID)
        mPresenter = RateAssignmentPresenterImpl(this)

        mBinding.rateAssignmentGradeEdtx.addTextChangedListener(mTextWatcher)
        mBinding.rateAssignmentSendBtn.setOnClickListener(this)
        mBinding.rateAssignmentCancelBtn.setOnClickListener(this)
    }

    fun setOnGradeSaved(callback: ItemCallback<Any>) {
        mCallback = callback
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rate_assignment_cancel_btn -> {
                dismiss()
            }

            R.id.rate_assignment_send_btn -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog.show(parentFragmentManager, null)

                val grade = mBinding.rateAssignmentGradeEdtx.text.toString()
                val gradeObj = Grade()
                gradeObj.grade = grade.toInt()
                mPresenter.rateAssignment(mAssignmentId, gradeObj)
            }
        }
    }

    fun onResponse(response: BaseResponse) {
        if (response.isSuccessful) {
            mCallback?.onItem(response)
            if (context == null) return
            mLoadingDialog.dismiss()
            dismiss()
        } else {
            if (context == null) return
            mLoadingDialog.dismiss()
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }
}