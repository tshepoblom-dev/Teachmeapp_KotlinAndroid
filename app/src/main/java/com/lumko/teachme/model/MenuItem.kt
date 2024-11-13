package com.lumko.teachme.model

import androidx.annotation.DrawableRes

class MenuItem {

    @DrawableRes
    var img: Int = 0

    lateinit var title: String

    var desc: String? = null

    var type = 0

    var progress = 0

    var isClickable = false

    constructor(img: Int, title: String) {
        this.img = img
        this.title = title
    }

    constructor()
}