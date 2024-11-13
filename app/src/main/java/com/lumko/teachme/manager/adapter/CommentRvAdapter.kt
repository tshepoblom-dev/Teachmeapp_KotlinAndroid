package com.lumko.teachme.manager.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lumko.teachme.R
import com.lumko.teachme.databinding.ItemCommentBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.model.Comment
import com.lumko.teachme.ui.widget.CommentMoreDialog

class CommentRvAdapter(
    comments: List<Comment>,
    private val fragmentManager: FragmentManager,
    private val isReply: Boolean
) :
    BaseArrayAdapter<Comment, CommentRvAdapter.ViewHolder>(comments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val comment = items[position]
        val context = viewholder.itemView.context

        if (isReply) {
            viewholder.binding.itemCommentContainer.setBackgroundResource(R.drawable.bordered_view_bg_white_1dp)
        }

        viewholder.binding.itemCommentTv.text = comment.comment
        viewholder.binding.itemCommentDateTv.text = Utils.getDateFromTimestamp(comment.createdAt)

        val author = comment.user
        viewholder.binding.itemCommentUserNameTv.text = author?.name
        viewholder.binding.itemCommentUserRoleTv.text = author?.roleName
        if (author?.avatar != null) {
            Glide.with(context).load(author.avatar).into(viewholder.binding.itemCommentUserImg)
        }

        if (comment.replies != null && comment.replies!!.isNotEmpty()) {
            val adapter = CommentRvAdapter(comment.replies!!, fragmentManager, true)
            viewholder.binding.itemCommentRepliesRv.adapter = adapter
            viewholder.binding.itemCommentRepliesRv.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, ItemCallback<Comment> {
        init {
            binding.itemCommentMoreBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val dialog = CommentMoreDialog()
            val bundle = Bundle()
            bundle.putParcelable(App.COMMENT, items[bindingAdapterPosition])

            dialog.arguments = bundle
            dialog.setCallback(this)
            dialog.show(fragmentManager, null)
        }

        override fun onItem(item: Comment, vararg args: Any) {
            if (isReply) {
                val adapter = binding.itemCommentRepliesRv.adapter as CommentRvAdapter
                adapter.items.add(item)
                adapter.notifyItemInserted(adapter.itemCount)
            } else {
                items.add(item)
                notifyItemInserted(itemCount)
            }
        }
    }
}