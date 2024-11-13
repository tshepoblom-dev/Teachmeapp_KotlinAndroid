package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.FragFinancialSalesHistoryBinding
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.adapter.SalesRvAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.SalesRes
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.FinancialSalesPresenterImpl
import com.lumko.teachme.ui.frag.abs.EmptyState

class FinancialSalesFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: FragFinancialSalesHistoryBinding
    private lateinit var mPresenter: Presenter.FinancialSalesPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragFinancialSalesHistoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = FinancialSalesPresenterImpl(this)
        mPresenter.getSales()
    }

    fun onSalesReceived(sales: SalesRes) {
        mBinding.financialSalesHistoryRvProgressBar.visibility = View.GONE
        if (sales.payouts.isNotEmpty()) {
            mBinding.financialSalesHistoryRv.adapter = SalesRvAdapter(sales.payouts)
        } else {
            showEmptyState()
        }

        mBinding.financialSalesHistoryClassSalesCountTv.text = sales.coursesSalesCount.toString()
        mBinding.financialSalesHistoryMeetingSalesCountTv.text = sales.meetingsSalesCount.toString()
        mBinding.financialSalesHistoryTotalSalesCountTv.text =
            ("${sales.coursesSalesCount + sales.meetingsSalesCount}")

        mBinding.financialSalesHistoryClassSalesTv.text =
            Utils.formatPrice(requireContext(), sales.classSales, false)
        mBinding.financialSalesHistoryMeetingSalesTv.text =
            Utils.formatPrice(requireContext(), sales.meetingSales, false)
        mBinding.financialSalesHistoryTotalSalesTv.text =
            Utils.formatPrice(requireContext(), sales.totalSales, false)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_sales, R.string.no_sales, R.string.no_sales_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialSalesHistoryEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

}