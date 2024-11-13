package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.model.Course

class CourseAddToFavFactory {
    companion object {
        fun getAddToFavObj(course: Course): BaseCourseAddToFav {
            if (course.isBundle()) {
                return BundleCourseAddToFav(course)
            }

            return NormalCourseAddToFav(course)
        }
    }
}