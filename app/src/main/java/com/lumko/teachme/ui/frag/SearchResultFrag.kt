package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragSearchResultBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.ViewPagerAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.*
import com.lumko.teachme.model.view.EmptyStateData
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.SearchResultPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.widget.LoadingDialog
import java.util.ArrayList

class SearchResultFrag : NetworkObserverFragment() {

    private lateinit var mBinding: FragSearchResultBinding
    private lateinit var mPresenter: Presenter.SearchResultPresenter
    private lateinit var mSearchQuery: String
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSearchResultBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.search_results)

        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        mSearchQuery = requireArguments().getString(App.KEY)!!

        mPresenter = SearchResultPresenterImpl(this)
        mPresenter.search(mSearchQuery)
    }

    fun onSearchResultReceived(data: Data<SearchObject>) {
        val courses = data.data!!.courses.items
        val coursesCount = data.data!!.courses.count

        val users = data.data!!.users.items.filter { it.isInstructor() }
        //val usersCount = data.data!!.users.count
        val usersCount = users.count()

        val organizations = data.data!!.organizations.items
        val organizationsCount = data.data!!.organizations.count

     /*   mBinding.searchResultTv.text =
            ("${coursesCount + usersCount + organizationsCount}" +
                    " ${getString(R.string.results_found_for)} \"$mSearchQuery\"")*/
        mBinding.searchResultTv.text =
            ("${usersCount}" +
                    " ${getString(R.string.results_found_for)} \"$mSearchQuery\"")

        val tabLayout = mBinding.searchResultTabContainer.tabLayout
        val viewPager = mBinding.searchResultTabContainer.viewPager

        var classesTitle = getString(R.string.classes)
        if (coursesCount > 0) {
            classesTitle += " (${coursesCount})"
        }

        //var usersTitle = getString(R.string.users)
        var usersTitle = "Tutors"
        if (usersCount > 0) {
            usersTitle += " (${usersCount})"
        }

        var organizationsTitle = getString(R.string.organizations)
        if (organizationsCount > 0) {
            organizationsTitle += " (${organizationsCount})"
        }

        val emptyStateData = EmptyStateData(
            R.drawable.no_result,
            R.string.result_no_found,
            R.string.try_more_for_search
        )

        val classesFrag = ClassesFrag()
        val usersFrag = UsersOrganizationsFrag()
        val organizationsFrag = UsersOrganizationsFrag()

        var bundle = Bundle()
        bundle.putParcelableArrayList(App.COURSES, courses as ArrayList<Course>)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        classesFrag.arguments = bundle

        bundle = Bundle()
        bundle.putParcelableArrayList(App.USERS, users as ArrayList<User>)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        usersFrag.arguments = bundle

        bundle = Bundle()
        bundle.putParcelableArrayList(App.USERS, organizations as ArrayList<User>)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        organizationsFrag.arguments = bundle

        val adapter = ViewPagerAdapter(childFragmentManager)
    //    adapter.add(classesFrag, classesTitle)
        adapter.add(usersFrag, usersTitle)
      //  adapter.add(organizationsFrag, organizationsTitle)

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        mLoadingDialog.dismiss()
    }

}