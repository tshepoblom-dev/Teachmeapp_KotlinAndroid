package com.lumko.teachme.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.databinding.ItemAttachmentBinding
import com.lumko.teachme.model.Attachment

class AttachmentRvAdapter(attachments: List<Attachment>) :
    BaseArrayAdapter<Attachment, AttachmentRvAdapter.ViewHolder>(attachments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAttachmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val attachment = items[position]
        viewholder.binding.itemAttachmentNameTv.text = attachment.title
        attachment.size?.let {
            viewholder.binding.itemAttachmentSizeTv.text = it
        }
    }

    class ViewHolder(val binding: ItemAttachmentBinding) : RecyclerView.ViewHolder(binding.root)
}