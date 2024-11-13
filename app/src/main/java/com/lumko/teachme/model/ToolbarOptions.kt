package com.lumko.teachme.model

import androidx.annotation.DrawableRes
import com.lumko.teachme.R

class ToolbarOptions {

    enum class Icon(@DrawableRes val icon: Int) {
        BACK(R.drawable.ic_arrow_left_black),
        NAV(R.drawable.ic_nav),
        CART(R.drawable.ic_cart_dark_blue),
        FILTER(R.drawable.ic_filters_black)
    }

    var startIcon: Icon? = null

    var endIcon: Icon? = null
}