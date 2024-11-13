package com.lumko.teachme.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.databinding.ItemPickerBinding
import com.lumko.teachme.model.ItemPicker

class PickerRvAdapter(
    private val rv: RecyclerView,
    items: List<ItemPicker>
) : BaseArrayAdapter<ItemPicker, PickerRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.pickerTv.text = items[position].itemTitle()
    }

    inner class ViewHolder(val binding: ItemPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.pickerTv.setOnClickListener {
                rv.smoothScrollToPosition(rv.getChildLayoutPosition(itemView))
            }
        }
    }
}