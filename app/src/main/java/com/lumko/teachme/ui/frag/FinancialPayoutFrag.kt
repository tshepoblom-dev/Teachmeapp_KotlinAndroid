package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.FragFinancialPayoutBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.adapter.CommonRvAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.PayoutAccount
import com.lumko.teachme.model.PayoutRes
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.FinancialPayoutPresenterImpl
import com.lumko.teachme.ui.frag.abs.EmptyState
import com.lumko.teachme.ui.widget.PayoutRequestDialog

class FinancialPayoutFrag : NetworkObserverFragment(), View.OnClickListener, EmptyState {

    private lateinit var mBinding: FragFinancialPayoutBinding
    private lateinit var mPresenter: Presenter.FinancialPayoutPresenter

    private lateinit var mPayoutAccount: PayoutAccount

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragFinancialPayoutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = FinancialPayoutPresenterImpl(this)
        mPresenter.getPayouts()
    }

    fun onPayoutsReceived(res: PayoutRes) {
        mPayoutAccount = res.payoutAccount
        mBinding.financialPayoutRvProgressBar.visibility = View.GONE
        if (res.payouts.isNotEmpty()) {
            mBinding.financialPayoutRv.adapter = CommonRvAdapter(res.payouts)
        } else {
            showEmptyState()
        }

        mBinding.financialPayoutAmountTv.text =
            Utils.formatPrice(requireContext(), res.payoutAccount.amonut, false)
        mBinding.financialPayoutRequestBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(App.PAYOUT_ACCOUT, mPayoutAccount)

        val dialog = PayoutRequestDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_payout, R.string.no_payout, R.string.no_payout_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialPayoutEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}