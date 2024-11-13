package com.lumko.teachme.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.databinding.ItemDialogSelectionBinding
import com.lumko.teachme.model.SelectionItem

class SelectionDialogRvAdapter(items: List<SelectionItem?>?) :
    BaseArrayAdapter<SelectionItem?, SelectionDialogRvAdapter.ViewHolder?>(
        items
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemDialogSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val title = items[position]?.title
        val img = items[position]?.img
        holder.binding.itemSelectionTv.text = "$title"
        if (img != null) {
            holder.binding.itemSelectionImg.setImageResource(img)
        } else {
            holder.binding.itemSelectionImg.visibility = View.GONE
        }
    }

    class ViewHolder(val binding: ItemDialogSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)
}