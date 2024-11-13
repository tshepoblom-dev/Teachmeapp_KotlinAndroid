package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lumko.teachme.R
import com.lumko.teachme.databinding.TabBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.ViewPagerAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.Meeting
import com.lumko.teachme.model.Meetings
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.MeetingsPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.widget.LoadingDialog
import java.util.ArrayList

class MeetingsTabFrag : NetworkObserverFragment() {

    private lateinit var mBinding: TabBinding
    private lateinit var mPresenter: Presenter.MeetingsPresenter
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.meetings)

        mPresenter = MeetingsPresenterImpl(this)
        mPresenter.getMeetings()
    }

    fun onMeetingsReceived(meetings: Meetings) {
        val reservedItems = meetings.reservations

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val reservedFrag = MeetingsFrag()

        var bundle = Bundle()
        bundle.putParcelableArrayList(App.MEETINGS, reservedItems.items as ArrayList<Meeting>)
        reservedFrag.arguments = bundle

        val adapter = ViewPagerAdapter(childFragmentManager)

        if (App.loggedInUser!!.isInstructor() || App.loggedInUser!!.isOrganizaton()) {
            val requestsFrag = MeetingsFrag()
            val requestItems = meetings.requests
            bundle = Bundle()
            bundle.putParcelableArrayList(App.MEETINGS, requestItems.items as ArrayList<Meeting>)
            requestsFrag.arguments = bundle
            adapter.add(requestsFrag, getString(R.string.requests))
        }
        adapter.add(reservedFrag, getString(R.string.reserved))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        mLoadingDialog.dismiss()
    }

}