package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.model.Course

class CourseDetailsFactory {
    companion object {
        fun getDetails(course: Course): BaseCourseDetails {
            if (course.isBundle()) {
                return BundleCourseDetails(course)
            }

            return NormalCourseDetails(course)
        }
    }
}