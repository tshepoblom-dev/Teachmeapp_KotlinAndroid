package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.TabBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.ViewPagerAdapter
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class MyClassesTabFrag : Fragment(), EmptyState, MainActivity.OnRefreshListener {

    private lateinit var mBinding: TabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.classes)

        initData()
    }

    private fun initData() {
        if (!App.isLoggedIn()) {
            showLoginState()
            return
        }

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val purchasedFrag = MyClassesFrag()
        var bundle = Bundle()
        bundle.putSerializable(App.SELECTION_TYPE, MyClassesFrag.Type.PURCHASED)

        purchasedFrag.arguments = bundle

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(purchasedFrag, getString(R.string.purchased))

        if (App.loggedInUser!!.isInstructor() || App.loggedInUser!!.isOrganizaton()) {

            bundle = Bundle()
            bundle.putSerializable(App.SELECTION_TYPE, MyClassesFrag.Type.MY_CLASSES)

            val myClassesFrag = MyClassesFrag()
            myClassesFrag.arguments = bundle

            bundle = Bundle()
            bundle.putSerializable(App.SELECTION_TYPE, MyClassesFrag.Type.ORGANIZATION)

            val organizationFrag = MyClassesFrag()
            organizationFrag.arguments = bundle

            bundle = Bundle()
            bundle.putSerializable(App.SELECTION_TYPE, MyClassesFrag.Type.INVITED)

            val invitedFrag = MyClassesFrag()
            invitedFrag.arguments = bundle

            adapter.add(myClassesFrag, getString(R.string.my_classes))
            adapter.add(organizationFrag, getString(R.string.organization))
            adapter.add(invitedFrag, getString(R.string.invited))
        } else {
            bundle = Bundle()
            bundle.putSerializable(App.SELECTION_TYPE, MyClassesFrag.Type.ORGANIZATION)

            val organizationFrag = MyClassesFrag()
            organizationFrag.arguments = bundle
            adapter.add(organizationFrag, getString(R.string.organization))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.tabEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        initData()
    }

}