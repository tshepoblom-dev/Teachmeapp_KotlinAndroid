package com.lumko.teachme.ui.frag.course

import com.lumko.teachme.model.AddToCart
import com.lumko.teachme.model.PricingPlan

abstract class BasePricingPlan {
    abstract fun getAddToCartItem(plan: PricingPlan?) : AddToCart
}