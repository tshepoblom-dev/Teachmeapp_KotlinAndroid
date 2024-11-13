package com.lumko.teachme.manager.adapter

import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.R
import com.lumko.teachme.databinding.ItemPricingPlanBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.model.PricingPlan
import java.lang.StringBuilder

class PricingPlanRvAdapter(
    pricingPlans: List<PricingPlan>,
    private val coursePoints: Int?,
    private val rv: RecyclerView
) :
    BaseArrayAdapter<PricingPlan, PricingPlanRvAdapter.ViewHolder>(pricingPlans) {

    private var mSelectedCallback: ItemCallback<PricingPlan>? = null
    private var mSelectedPosition: Int? = null

    fun getSelectedItem(): PricingPlan? {
        return if (mSelectedPosition == null) null else items[mSelectedPosition!!]
    }

    fun setOnItemSelectedListener(callback: ItemCallback<PricingPlan>) {
        mSelectedCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPricingPlanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = items[position]
        val context = holder.itemView.context

        holder.init(plan)

        var titleBuilder = StringBuilder(plan.title)
        if (plan.discount > 0) {
            titleBuilder = titleBuilder.append("(${plan.discount}%)")
                .append(context.getString(R.string.off))
        }

        holder.binding.itemPricingPlanTitleTv.text = titleBuilder.toString()
        holder.binding.itemPricingPlanDescTv.text = plan.description
        holder.binding.itemPricingPlanPriceTv.text =
            if (plan.isPointsItem) plan.price.toInt()
                .toString() else Utils.formatPrice(holder.itemView.context, plan.price)

        if (mSelectedPosition == position) {
            val transition = holder.itemView.background as TransitionDrawable
            transition.startTransition(300)
        } else {
            val bg = if (plan.isPointsItem) {
                holder.binding.itemPricingPlanPriceTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.orange
                    )
                )
                holder.binding.itemPricingPlanImg.setBackgroundResource(R.drawable.circle_orange)
                holder.binding.itemPricingPlanImg.setImageResource(R.drawable.ic_star_with_gradient)
                R.drawable.bg_transition_border_gray81_to_orange
            } else {
                R.drawable.bg_transition_border_gray81_to_accent
            }

            holder.itemView.background = ContextCompat.getDrawable(
                holder.itemView.context, bg
            ) as TransitionDrawable
        }
    }

    inner class ViewHolder(val binding: ItemPricingPlanBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var mIsInit = false

        fun init(plan: PricingPlan) {
            if (mIsInit) return

            mIsInit = true

            if (plan.isValid || plan.isPointsItem) {

                val bg = if (plan.isPointsItem) {
                    R.drawable.bg_transition_border_gray81_to_orange
                } else {
                    R.drawable.bg_transition_border_gray81_to_accent
                }

                val transitionDrawable = ContextCompat.getDrawable(
                    itemView.context,
                    bg
                ) as TransitionDrawable

                itemView.background = transitionDrawable

                itemView.setOnClickListener(this)
            } else {
                binding.itemPricingPlanImg.setBackgroundResource(R.drawable.circle_gray81)
            }
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            val plan = items[position]
            val context = itemView.context

            if (plan.isPointsItem && App.quickInfo!!.availablePoints < coursePoints!!) {
                ToastMaker.show(
                    context,
                    context.getString(R.string.error),
                    context.getString(R.string.you_donnot_have_enough_points),
                    ToastMaker.Type.ERROR
                )
                return
            }

            if (mSelectedPosition != null) {
                reverseTransition()

                if (mSelectedPosition == position) {
                    mSelectedPosition = null
                    plan.removed = true
                    mSelectedCallback?.onItem(plan)
                    return
                }
            }


            val transition = itemView.background as TransitionDrawable
            transition.startTransition(300)

            mSelectedPosition = position
            mSelectedCallback?.onItem(plan)
        }

        private fun reverseTransition() {
            try {
                val viewHolder =
                    rv.findViewHolderForAdapterPosition(mSelectedPosition!!) as ViewHolder
                (viewHolder.itemView.background as TransitionDrawable).reverseTransition(
                    300
                )
            } catch (ex: Exception) {
            }
        }
    }
}