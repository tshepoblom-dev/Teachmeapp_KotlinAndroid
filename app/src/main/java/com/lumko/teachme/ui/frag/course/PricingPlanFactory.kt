package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.model.Course

class PricingPlanFactory {
    companion object {
        fun getPlan(course: Course): BasePricingPlan {
            if (course.isBundle()) {
                return BundlePricingPlan(course)
            }

            return NormalPricingPlan(course)
        }
    }
}