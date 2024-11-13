package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.model.AddToFav
import com.lumko.teachme.model.Course

class BundleCourseAddToFav(val course: Course) : BaseCourseAddToFav() {

    override fun getAddToFavItem(): AddToFav {
        val addToFav = AddToFav()
        addToFav.itemId = course.id
        addToFav.itemName = AddToFav.ItemType.BUNDLE.value
        return addToFav
    }
}