package com.lumko.teachme.ui.frag

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lumko.teachme.R
import com.lumko.teachme.databinding.FragCourseOverviewBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.presenterImpl.CommonApiPresenterImpl
import com.lumko.teachme.ui.MainActivity

class CourseOverviewFrag : Fragment(), View.OnClickListener, ItemCallback<Course> {

    private lateinit var mBinding: FragCourseOverviewBinding
    private lateinit var mCourse: Course

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragCourseOverviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!

        val presenter = CommonApiPresenterImpl.getInstance()
        presenter.getCourseDetails(mCourse.id, this)

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(
            toolbarOptions,
            ("${getCourseTitle()} ${getString(R.string.overview)}")
        )

        mBinding.courseOverviewViewBtn.setOnClickListener(this)
        mBinding.courseOverviewViewBtn.text = ("${getString(R.string.view)} ${getCourseTitle()}")

        initItems()
    }

    private fun initItems() {
        if (mCourse.img != null) {
            Glide.with(requireContext()).load(mCourse.img)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mBinding.courseOverviewImgOverlay.visibility = View.VISIBLE
                        return false
                    }

                }).into(mBinding.courseOverviewImg)
        }

        mBinding.courseOverviewRatingBar.rating = mCourse.rating
        mBinding.courseOverviewRatingCountTv.text = mCourse.reviewsCount.toString()

        mBinding.courseOverviewTitleTv.text = mCourse.title
        mBinding.courseOverviewTv.text = ("${getCourseTitle()} ${getString(R.string.overview)}")

        mBinding.courseOverviewClassIDTv.text = mCourse.id.toString()
        mBinding.courseOverviewCategoryTv.text = mCourse.category
        mBinding.courseOverviewSalesTv.text =
            Utils.formatPrice(requireContext(), mCourse.sales!!.amount, false)
        mBinding.courseOverviewStudentsTv.text = mCourse.studentsCount.toString()
        mBinding.courseOverviewEighthItemValueTv.text =
            Utils.getDateFromTimestamp(mCourse.createdAt)

        mBinding.courseOverviewSeventhItemValueTv.text =
            Utils.getDuration(requireContext(), mCourse.duration)
    }

    private fun getCourseTitle(): String {
        return when (mCourse.type) {
            Course.Type.WEBINAR.value -> getString(R.string.overview)
            Course.Type.COURSE.value -> getString(R.string.course)
            else -> getString(R.string.text_course)
        }
    }


    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, mCourse)

        val frag = CourseDetailsFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onItem(c: Course, vararg args: Any) {
        if (context == null) return
        mBinding.courseOverviewSessionsTv.text = c.sessionesCount.toString()
        mBinding.courseOverviewQuizzesTv.text = c.quizzes.size.toString()
    }
}