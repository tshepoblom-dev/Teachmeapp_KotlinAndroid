package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.databinding.FragCourseDetailsInformationBinding
import com.lumko.teachme.model.view.CourseCommonItem

abstract class BaseCourseDetailsInformation() {
    abstract fun getInfoList(): ArrayList<CourseCommonItem>
    abstract fun setMarkInfo(binding: FragCourseDetailsInformationBinding)
}