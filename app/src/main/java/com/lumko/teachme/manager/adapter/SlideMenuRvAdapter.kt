package com.lumko.teachme.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.databinding.ItemSlideMenuBinding
import com.lumko.teachme.model.MenuItem

class SlideMenuRvAdapter(items: List<MenuItem>) :
    BaseArrayAdapter<MenuItem, SlideMenuRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSlideMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.slideMenuItemImg.setImageResource(item.img)
        holder.binding.slideMenuItemTitleTv.text = item.title
    }

    inner class ViewHolder(val binding: ItemSlideMenuBinding) :
        RecyclerView.ViewHolder(binding.root)
}