package com.lumko.teachme.model.view

import com.lumko.teachme.model.Category
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class CategoryView(title: String, items: List<Category>) : ExpandableGroup<Category>(title, items) {

    var subCats = items

    var id = 0

    var catTitle: String = ""

    var icon: String? = null

    var count = 0

    var color: String? = null

    var expanded = false
}