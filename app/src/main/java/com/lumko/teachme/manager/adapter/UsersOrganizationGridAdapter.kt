package com.lumko.teachme.manager.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lumko.teachme.R
import com.lumko.teachme.databinding.ItemUserBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.net.ApiService.activity
import com.lumko.teachme.model.User
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.ProfileFrag

class UsersOrganizationGridAdapter(items: List<User>) :
    BaseArrayAdapter<User, UsersOrganizationGridAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item.avatar != null)
            Glide.with(holder.itemView.context).load(item.avatar).into(holder.binding.itemUserImg)
        holder.binding.itemUserNameTv.text = item.name
        holder.binding.itemUserRoleTv.text = item.bio
        holder.binding.itemUserRatingBar.rating = item.rating
        holder.binding.itemUserActionButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putParcelable(App.USER, item)

            val frag = ProfileFrag()
            frag.arguments = bundle
            (activity as MainActivity).transact(frag)
        };
        when (item?.meetingStatus) {
            User.MeetingStatus.NO.status -> {
                holder.binding.itemUserMeetingImg.setBackgroundResource(R.drawable.circle_gray)
                holder.binding.itemUserMeetingImg.setImageResource(R.drawable.ic_calendar)
            }
            User.MeetingStatus.AVAILABLE.status -> {
                holder.binding.itemUserMeetingImg.setBackgroundResource(R.drawable.circle_light_green)
                holder.binding.itemUserMeetingImg.setImageResource(R.drawable.ic_calendar_green)
            }
            User.MeetingStatus.UNAVAILABLE.status -> {
                holder.binding.itemUserMeetingImg.setBackgroundResource(R.drawable.circle_light_red)
                holder.binding.itemUserMeetingImg.setImageResource(R.drawable.ic_calendar_red)
            }
        }
    }

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
}