package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogCouponBinding
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.net.observer.NetworkObserverBottomSheetDialog
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.CouponPresenterImpl

class CouponDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogCouponBinding
    private lateinit var mPresenter: Presenter.CouponPresenter
    private var mCallback: ItemCallback<CouponValidation>? = null

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            mBinding.couponValidateBtn.isEnabled = mBinding.couponEdtx.text.toString().isNotEmpty()
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
    ): View? {
        mBinding = DialogCouponBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = CouponPresenterImpl(this)

        mBinding.couponEdtx.addTextChangedListener(mTextWatcher)
        mBinding.couponValidateBtn.setOnClickListener(this)
        mBinding.couponCancelBtn.setOnClickListener(this)
    }

    fun setOnCouponAdded(callback: ItemCallback<CouponValidation>) {
        mCallback = callback
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.couponCancelBtn -> {
                dismiss()
            }

            R.id.couponValidateBtn -> {
                val coupon = mBinding.couponEdtx.text.toString()
                val couponObj = Coupon()
                couponObj.coupon = coupon
                mPresenter.validateCoupon(couponObj)
            }
        }
    }

    fun onCouponValidated(data: Data<CouponValidation>) {
        if (data.isSuccessful) {
            mCallback?.onItem(data.data!!)
            if (context == null) return
            dismiss()
        } else {
            if (context == null) return
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}