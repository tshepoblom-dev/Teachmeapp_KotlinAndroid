package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogCourseDetailsMoreBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.net.observer.NetworkObserverBottomSheetDialog
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Course
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.CourseDetailsMorePresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.course.BaseCourseAddToFav
import com.lumko.teachme.ui.frag.course.CourseAddToFavFactory
import java.util.*


class ClassDetailsMoreDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogCourseDetailsMoreBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: Presenter.CourseDetailsMorePresenter
    private lateinit var mAddToFavObj: BaseCourseAddToFav

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogCourseDetailsMoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!
        mPresenter = CourseDetailsMorePresenterImpl(this)
        mAddToFavObj = CourseAddToFavFactory.getAddToFavObj(mCourse)

        if (!mCourse.isLive()) {
            mBinding.courseDetailsMoreAddToCalendarBtn.visibility = View.GONE
        }

        if (mCourse.isFavorite) {
            mBinding.courseDetailsMoreAddToFavoritesBtn.text =
                getString(R.string.remove_from_favorites)
        }

        if (mCourse.link.isNullOrEmpty()) {
            mBinding.courseDetailsMoreShareBtn.visibility = View.GONE
        }

        if (mCourse.isBundle()) {
            mBinding.courseDetailsMoreReportBtn.visibility = View.GONE
        }

        mBinding.courseDetailsMoreAddToCalendarBtn.setOnClickListener(this)
        mBinding.courseDetailsMoreAddToFavoritesBtn.setOnClickListener(this)
        mBinding.courseDetailsMoreShareBtn.setOnClickListener(this)
        mBinding.courseDetailsMoreReportBtn.setOnClickListener(this)
        mBinding.courseDetailsMoreCancelBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id != R.id.course_details_more_cancel_btn && !App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage(null)
            return
        }

        when (v?.id) {
            R.id.course_details_more_add_to_calendar_btn -> {
                Utils.addToCalendar(
                    requireContext(),
                    mCourse.title,
                    mCourse.startDate * 1000,
                    mCourse.startDate * 1000
                )
            }

            R.id.course_details_more_add_to_favorites_btn -> {
                mPresenter.addToFavorite(mAddToFavObj.getAddToFavItem())
            }

            R.id.course_details_more_share_btn -> {
                Utils.shareLink(requireContext(), mCourse.title, mCourse.link!!)
            }

            R.id.course_details_more_report_btn -> {
                val bundle = Bundle()
                bundle.putSerializable(App.SELECTION_TYPE, CommentDialog.Type.REPORT_COURSE)
                bundle.putInt(App.ID, mCourse.id)

                val reportDialog = CommentDialog()
                reportDialog.arguments = bundle
                reportDialog.show(childFragmentManager, null)
            }

            R.id.course_details_more_cancel_btn -> {
                dismiss()
            }
        }
    }


    fun onItemAddedToFavorites(response: BaseResponse) {
        if (context == null) return

        if (response.isSuccessful) {
            if (mBinding.courseDetailsMoreAddToFavoritesBtn.text == getString(R.string.add_to_favorites)) {
                mBinding.courseDetailsMoreAddToFavoritesBtn.text =
                    getString(R.string.remove_from_favorites)
            } else {
                mBinding.courseDetailsMoreAddToFavoritesBtn.text =
                    getString(R.string.add_to_favorites)
            }
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}