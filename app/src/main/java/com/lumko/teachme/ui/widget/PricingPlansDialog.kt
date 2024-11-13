package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogPricingPlansBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.adapter.PricingPlanRvAdapter
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.net.observer.NetworkObserverBottomSheetDialog
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.PricingPlan
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.CommonApiPresenterImpl
import com.lumko.teachme.presenterImpl.PricingPlansPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.CourseDetailsFrag
import com.lumko.teachme.ui.frag.course.BasePricingPlan
import com.lumko.teachme.ui.frag.course.PricingPlanFactory
import java.lang.StringBuilder
import java.util.ArrayList

class PricingPlansDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    ItemCallback<PricingPlan> {

    private lateinit var mBinding: DialogPricingPlansBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: Presenter.PricingPlansPresenter
    private lateinit var mPricingPlanObj: BasePricingPlan

    private val mResCallback = object : ItemCallback<BaseResponse> {
        override fun onItem(item: BaseResponse, vararg args: Any) {
            if (context == null) return

            if (item.isSuccessful) {
                (activity as MainActivity).updateCart()
                dismiss()
            } else {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.error),
                    item.message,
                    ToastMaker.Type.ERROR
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogPricingPlansBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!
        mPricingPlanObj = PricingPlanFactory.getPlan(mCourse)

        val plans = getPlans()

        val adapter = PricingPlanRvAdapter(plans, mCourse.points, mBinding.pricingPlanRv)
        adapter.setOnItemSelectedListener(this)
        mBinding.pricingPlanRv.adapter = adapter

        mBinding.pricingPlanCancelBtn.setOnClickListener(this)
        mBinding.pricingPlanAddToCartBtn.setOnClickListener(this)
        setPrice()
    }

    private fun getPlans(): List<PricingPlan> {
        val plans = ArrayList<PricingPlan>()

        if (mCourse.canBuyWithPoints) {
            mPresenter = PricingPlansPresenterImpl(this)

            val pointsPlan = PricingPlan()
            pointsPlan.title = getString(R.string.buy_with_points)
            pointsPlan.description =
                StringBuilder(getString(R.string.you_have)).append(" ")
                    .append(App.quickInfo!!.availablePoints).append(" ")
                    .append(getString(R.string.reward_points_lower)).append(".").toString()

            pointsPlan.price = mCourse.points!!.toDouble()
            pointsPlan.isPointsItem = true
            plans.add(pointsPlan)
        }

        plans.addAll(mCourse.pricingPlans)
        return plans
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pricing_plan_cancel_btn -> {
                dismiss()
            }

            R.id.pricing_plan_add_to_cart_btn -> {
                val plan =
                    (mBinding.pricingPlanRv.adapter as PricingPlanRvAdapter).getSelectedItem()

                if (plan != null) {
                    if (plan.isPointsItem) {
                        mPresenter.purchaseWithPoints(mCourse)
                    } else {
                        val presenter = CommonApiPresenterImpl.getInstance()
                        presenter.addToCart(mPricingPlanObj.getAddToCartItem(plan), mResCallback)
                    }
                } else {
                    val presenter = CommonApiPresenterImpl.getInstance()
                    presenter.addToCart(mPricingPlanObj.getAddToCartItem(null), mResCallback)
                }
            }
        }
    }

    override fun onItem(item: PricingPlan, vararg args: Any) {
        if (item.removed) {
            setPrice()
            item.removed = false
            return
        }

        if (item.isPointsItem) {
            mBinding.pricingPlanAddToCartBtn.text = getString(R.string.purchase)
        } else {
            mBinding.pricingPlanAddToCartBtn.text = getString(R.string.add_to_cart)
        }
    }

    private fun setPrice() {
        val price = if (mCourse.priceWithDiscount != mCourse.price && mCourse.discount > 0) {
            mCourse.priceWithDiscount
        } else {
            mCourse.price
        }

        val priceTxt = getString(R.string.add_to_cart) +
                " (${Utils.formatPrice(requireContext(), price)})"
        mBinding.pricingPlanAddToCartBtn.text = priceTxt
    }

    fun onPurchase(response: BaseResponse) {
        if (response.isSuccessful) {
            (parentFragment as CourseDetailsFrag).refreshPage()
            dismiss()
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

}