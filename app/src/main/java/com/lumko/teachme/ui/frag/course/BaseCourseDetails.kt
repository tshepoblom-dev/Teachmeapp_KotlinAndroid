package com.lumko.teachme.ui.frag.course

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.lumko.teachme.manager.adapter.ViewPagerAdapter
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.model.AddToCart
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Review

abstract class BaseCourseDetails {
    abstract fun getToolbarTitle(): Int
    abstract fun getTabsAdapter(
        context: Context,
        fragmentManager: FragmentManager,
    ): ViewPagerAdapter
    abstract fun getCourseDetails(
        id: Int,
        callback: ItemCallback<Course>
    )
    abstract fun getAddToCartItem() : AddToCart
    abstract fun getBaseReviewObj() : Review
    abstract fun getCourseType(): String
}