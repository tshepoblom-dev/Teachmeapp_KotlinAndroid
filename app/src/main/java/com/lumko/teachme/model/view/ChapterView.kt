package com.lumko.teachme.model.view

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class ChapterView(title: String, val description: String, items: List<CourseCommonItem>) :
    ExpandableGroup<CourseCommonItem>(title, items)