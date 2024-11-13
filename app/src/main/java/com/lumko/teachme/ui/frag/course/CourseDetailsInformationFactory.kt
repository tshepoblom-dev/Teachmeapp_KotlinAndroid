package com.lumko.teachme.ui.frag.course

import android.content.Context
import com.lumko.teachme.model.Course

class CourseDetailsInformationFactory {
    companion object {
        fun getInformation(course: Course, context: Context): BaseCourseDetailsInformation {
            if (course.isBundle()) {
                return BundleCourseDetailsInformation(context, course)
            }

            return NormalCourseDetailsInformation(context, course)
        }
    }
}