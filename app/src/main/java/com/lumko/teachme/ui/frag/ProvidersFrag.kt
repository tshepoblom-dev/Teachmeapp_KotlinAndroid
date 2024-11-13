package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.lumko.teachme.R
import com.lumko.teachme.databinding.TabBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.ViewPagerAdapter
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.model.*
import com.lumko.teachme.model.view.EmptyStateData
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.widget.ProvidersFiltersDialog
import java.lang.Exception
import kotlin.collections.ArrayList

class ProvidersFrag : Fragment(), ItemCallback<ArrayList<KeyValuePair>>,
    TabLayout.OnTabSelectedListener {

    private lateinit var mBinding: TabBinding
    private var mSelectedList: ArrayList<KeyValuePair>? = null
    private var mSelected: UsersOrganizationsFrag? = null

    private val mFilterClickCallback = object : ItemCallback<Any> {
        override fun onItem(item: Any, vararg args: Any) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(App.SELECTED, mSelectedList)

            val dialog = ProvidersFiltersDialog()
            dialog.arguments = bundle
            dialog.setCallback(this@ProvidersFrag)
            dialog.show(childFragmentManager, null)
        }
    }

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
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV
        toolbarOptions.endIcon = ToolbarOptions.Icon.FILTER

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.providers)
        (activity as MainActivity).setContainer(false)


        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val instructorsBundle = Bundle()
        instructorsBundle.putParcelable(
            App.EMPTY_STATE, EmptyStateData(
                R.drawable.no_instructor,
                R.string.no_instructor,
                R.string.no_instructor_yet
            )
        )
        instructorsBundle.putSerializable(
            App.SELECTION_TYPE,
            UsersOrganizationsFrag.ProviderType.INSTRUCTORS
        )

        val consultantsBundle = Bundle()
        consultantsBundle.putParcelable(
            App.EMPTY_STATE, EmptyStateData(
                R.drawable.no_instructor,
                R.string.result_no_found,
                R.string.no_consultants_yet
            )
        )

        /*
        consultantsBundle.putSerializable(
            App.SELECTION_TYPE,
            UsersOrganizationsFrag.ProviderType.CONSULTANTS
        )

        val organizationsBundle = Bundle()
        organizationsBundle.putParcelable(
            App.EMPTY_STATE, EmptyStateData(
                R.drawable.no_instructor,
                R.string.result_no_found,
                R.string.no_organizations_yet
            )
        )
        organizationsBundle.putSerializable(
            App.SELECTION_TYPE,
            UsersOrganizationsFrag.ProviderType.ORGANIZATIONS
        )
*/
        val instructorsFrag = UsersOrganizationsFrag()
      //  val organizationsFrag = UsersOrganizationsFrag()
        //val consultantsFrag = UsersOrganizationsFrag()

        instructorsFrag.arguments = instructorsBundle
     //  organizationsFrag.arguments = organizationsBundle
       // consultantsFrag.arguments = consultantsBundle

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(instructorsFrag, getString(R.string.instructors))
       // adapter.add(organizationsFrag, getString(R.string.organizations))
     //   adapter.add(consultantsFrag, getString(R.string.consultants))

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)

        (activity as MainActivity).setOnFilterButtonClickListener(mFilterClickCallback)
    }

    fun updateTab(
        type: UsersOrganizationsFrag.ProviderType,
        count: Int
    ) {
        when (type) {
            UsersOrganizationsFrag.ProviderType.CONSULTANTS -> {
                mBinding.tabLayout.getTabAt(2)?.text =
                    "${getString(R.string.consultants)} (${count})"
            }
            UsersOrganizationsFrag.ProviderType.ORGANIZATIONS -> {
                mBinding.tabLayout.getTabAt(1)?.text =
                    "${getString(R.string.organizations)} (${count})"
            }
            UsersOrganizationsFrag.ProviderType.INSTRUCTORS -> {
                mBinding.tabLayout.getTabAt(0)?.text =
                    "${getString(R.string.instructors)} (${count})"
            }
        }
    }


    override fun onItem(item: ArrayList<KeyValuePair>, vararg args: Any) {
        mSelectedList = item
        val viewPagerAdapter = mBinding.viewPager.adapter as ViewPagerAdapter
        mSelected =
            viewPagerAdapter.getItem(mBinding.viewPager.currentItem) as UsersOrganizationsFrag

        mSelected?.filter(item)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if (mSelectedList != null) {
            try {
                mSelected?.filter(null)
            } catch (ex: Exception) {
            }
        }

        mSelected = null
        mSelectedList = null
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
    }
}