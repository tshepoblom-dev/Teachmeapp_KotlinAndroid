package com.lumko.teachme.manager.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lumko.teachme.databinding.ItemFeaturedCourseBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.PrerequisiteCourse
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.CourseDetailsFrag

class FeaturedSliderAdapter<T>(items: List<T>, private val activity: MainActivity) :
    BaseArrayAdapter<T, FeaturedSliderAdapter<T>.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeaturedCourseBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        val context = viewHolder.itemView.context

        val course: Course = when (item) {
            is PrerequisiteCourse -> {
                item.course
            }
            is Course -> {
                item
            }
            else -> {
                return
            }
        }

        if (!course.img.isNullOrEmpty()) {
            Glide.with(context).load(course.img).addListener(object : RequestListener<Drawable>{
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
                    viewHolder.binding.itemFeaturedImgOverlay.visibility = View.VISIBLE
                    return false
                }

            })
                .into(viewHolder.binding.itemFeaturedImg)
        }

        viewHolder.binding.itemFeaturedTitleTv.text = course.title
        viewHolder.binding.itemFeaturedPriceTv.text = Utils.formatPrice(context, course.price)
        viewHolder.binding.itemFeaturedRatingBar.rating = course.rating

        viewHolder.binding.itemFeaturedDurationTv.text =
            Utils.getFormattedDuration(viewHolder.itemView.context, course.duration)

        viewHolder.binding.itemFeaturedInstructorNameTv.text = course.teacher.name
        if (!course.teacher.avatar.isNullOrEmpty())
            Glide.with(viewHolder.itemView.context).load(course.teacher.avatar)
                .into(viewHolder.binding.itemFeaturedInstructorImg)

        if (item is PrerequisiteCourse) {
            viewHolder.binding.itemFeaturedRequiredTv.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(val binding: ItemFeaturedCourseBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val course: Course = when (val item = items[bindingAdapterPosition]) {
                is PrerequisiteCourse -> {
                    item.course
                }
                is Course -> {
                    item
                }
                else -> {
                    return
                }
            }

            val bundle = Bundle()
            bundle.putParcelable(App.COURSE, course)

            val frag = CourseDetailsFrag()
            frag.arguments = bundle
            activity.transact(frag)
        }
    }
}