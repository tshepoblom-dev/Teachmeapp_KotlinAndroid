package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.RvBinding
import com.lumko.teachme.manager.adapter.BankInfoRvAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.SystemBankAccount
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.presenterImpl.BanksInfoPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class BanksInfoFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: RvBinding

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
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.bank_accounts)

        val presenter = BanksInfoPresenterImpl(this)
        presenter.getBanksInfo()
    }

    fun onInfosReceived(items: List<SystemBankAccount>) {
        mBinding.rvProgressBar.visibility = View.GONE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = BankInfoRvAdapter(items)
        } else {
            showEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_offline_payments, R.string.no_bank_accounts, R.string.no_bank_accounts_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}