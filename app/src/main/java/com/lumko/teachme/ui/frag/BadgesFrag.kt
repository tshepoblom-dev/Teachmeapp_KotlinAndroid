package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.RvBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.UserBadgeGridAdapter
import com.lumko.teachme.model.UserBadge
import com.lumko.teachme.ui.frag.abs.EmptyState

class BadgesFrag : Fragment(), EmptyState {

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
        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.isNestedScrollingEnabled = true

        val badges = requireArguments().getParcelableArrayList<UserBadge>(App.BADGES)!!
        if (badges.isNotEmpty()) {
            val adapter = UserBadgeGridAdapter(badges)
            mBinding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
            mBinding.rv.adapter = adapter
        } else {
            showEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_badges, R.string.no_badges, R.string.no_badges_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}