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
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.AssignmentsRvAdapter
import com.lumko.teachme.manager.listener.ItemClickListener
import com.lumko.teachme.manager.listener.OnItemClickListener
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.Assignment
import com.lumko.teachme.presenterImpl.MyAssignmentsPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class MyAssignmentsFrag : NetworkObserverFragment(), EmptyState, OnItemClickListener {

    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val presenter = MyAssignmentsPresenterImpl(this)
        presenter.getMyAssignments()
    }

    fun onAssignmentsReceived(assignments: List<Assignment>) {
        mBinding.rvProgressBar.visibility = View.GONE

        if (assignments.isEmpty()) {
            showEmptyState()
        } else {
            val rv = mBinding.rv
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = AssignmentsRvAdapter(assignments)
            rv.addOnItemTouchListener(ItemClickListener(rv, this))
        }
    }

    private fun showEmptyState() {
        showEmptyState(
            R.drawable.no_submission,
            R.string.no_assignments,
            R.string.no_assignments_student_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val assignment = (mBinding.rv.adapter as AssignmentsRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.ITEM, assignment)

        val overviewFrag = AssignmentOverviewFrag()
        overviewFrag.arguments = bundle
        (activity as MainActivity).transact(overviewFrag)
    }
}