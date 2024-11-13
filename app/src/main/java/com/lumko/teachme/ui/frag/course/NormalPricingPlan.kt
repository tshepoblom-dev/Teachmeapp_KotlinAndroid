package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.model.AddToCart
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.PricingPlan

class NormalPricingPlan(val course: Course) : BasePricingPlan() {

    override fun getAddToCartItem(plan: PricingPlan?): AddToCart {
        val addToCart = AddToCart()
        plan?.let { addToCart.pricingPlanId = it.id }
        addToCart.itemId = course.id
        addToCart.itemName = AddToCart.ItemType.WEBINAR.value
        return addToCart
    }

}