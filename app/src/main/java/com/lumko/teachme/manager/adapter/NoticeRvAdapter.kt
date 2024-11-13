package com.lumko.teachme.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumko.teachme.R
import com.lumko.teachme.databinding.ItemNoticeBinding
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.model.Notice

class NoticeRvAdapter(notices: List<Notice>) :
    BaseArrayAdapter<Notice, NoticeRvAdapter.ViewHolder>(notices) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoticeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val notice = items[position]
        val binding = viewholder.binding

        binding.itemNoticeTitleTv.text = notice.title
        binding.itemNoticeCreatorNameTv.text = notice.creator.name
        binding.itemNoticeDateTv.text = Utils.getDateFromTimestamp(notice.createdAt)

        val bg: Int
        val img : Int

        when (notice.color) {
            Notice.ColorType.DANGER.value -> {
                bg = R.drawable.round_view_red_corner10
                img = R.drawable.ic_danger
            }

            Notice.ColorType.WARNING.value -> {
                bg = R.drawable.round_view_orange_corner10
                img = R.drawable.ic_warning
            }

            Notice.ColorType.NEUTRAL.value -> {
                bg = R.drawable.round_view_gray_corner10
                img = R.drawable.ic_more_circle
            }

            Notice.ColorType.INFO.value -> {
                bg = R.drawable.round_view_blue_corner10
                img = R.drawable.ic_info_white
            }

            Notice.ColorType.SUCCESS.value -> {
                bg = R.drawable.round_view_accent_corner10
                img = R.drawable.ic_check_circle_green
            }

            else -> {
                bg = R.drawable.round_view_gray_corner10
                img = R.drawable.ic_more_circle
            }
        }

        binding.itemNoticeImg.setBackgroundResource(bg)
        binding.itemNoticeImg.setImageResource(img)
    }

    class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root)
}