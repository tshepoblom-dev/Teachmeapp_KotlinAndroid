package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragResetPasswordBinding
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.ForgetPassword
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.ForgotPasswordPresenterImpl

class ForgetPasswordFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mBinding: FragResetPasswordBinding
    private lateinit var mPresenter: Presenter.ForgotPasswordPresenter

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableLoginBtn()
        }
    }

    private fun enableDisableLoginBtn() {
        val email = mBinding.resetPasswordEmailPhoneEdtx.text.toString()
        val isValidEmail = email.length >= 3 && email.contains("@")
                && email.contains(".")
        mBinding.resetPasswordBtn.isEnabled = isValidEmail
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragResetPasswordBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = ForgotPasswordPresenterImpl(this)
        mBinding.resetPasswordEmailPhoneEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.resetPasswordLoginBtn.setOnClickListener(this)
        mBinding.resetPasswordBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.resetPasswordLoginBtn -> {
                activity?.onBackPressed()
            }

            R.id.resetPasswordBtn -> {
                val email = mBinding.resetPasswordEmailPhoneEdtx.text.toString()
                val forgetPassword = ForgetPassword()
                forgetPassword.email = email
                mPresenter.sendChangePasswordLink(forgetPassword)
            }
        }
    }

    fun onPasswordChanged(response: BaseResponse) {
        if (response.isSuccessful) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.success),
                response.message,
                ToastMaker.Type.SUCCESS
            )
            activity?.onBackPressed()
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}