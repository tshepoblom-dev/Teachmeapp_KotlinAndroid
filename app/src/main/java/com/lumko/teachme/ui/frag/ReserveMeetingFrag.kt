package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragReserveMeetingBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.Utils.toBoolean
import com.lumko.teachme.model.MeetingReserve
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.widget.ReserveMeetingDialog

class ReserveMeetingFrag : Fragment() {

    private lateinit var mBinding: FragReserveMeetingBinding
    private var mMeetingReserve: MeetingReserve? = null
    private var mUserId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragReserveMeetingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mMeetingReserve = requireArguments().getParcelable(App.MEETING)
        mUserId = requireArguments().getInt(App.USER_ID)

        if (mMeetingReserve == null || mMeetingReserve!!.disabled.toBoolean() || mMeetingReserve!!.timings.size == 0) {
            val unavailbeTv =
                getString(R.string.meeting) + " " + getString(R.string.is_not_available)
            mBinding.reserveMeetingTv.text = unavailbeTv
            return
        }

        val price =
            if (mMeetingReserve!!.price != mMeetingReserve!!.priceWithDiscount && mMeetingReserve!!.discount > 0) {
                mMeetingReserve!!.priceWithDiscount
            } else {
                mMeetingReserve!!.price
            }

        val meetigTv =
            "${getString(R.string.reserve_a_meeting_with_user)}\n${getString(R.string.hourly_charge)}: " + Utils.formatPrice(
                requireContext(),
                price
            )
        mBinding.reserveMeetingTv.text = meetigTv
    }

    fun onMeetingReserve() {
        if (!App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage()
            return
        }

        val bundle = Bundle()
        bundle.putParcelable(App.MEETING, mMeetingReserve)
        bundle.putInt(App.USER_ID, mUserId)

        val dialog = ReserveMeetingDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

}