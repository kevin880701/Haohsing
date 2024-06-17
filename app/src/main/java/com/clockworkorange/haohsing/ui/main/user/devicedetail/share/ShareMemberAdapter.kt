package com.clockworkorange.haohsing.ui.main.user.devicedetail.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.device.ShareMember
import com.clockworkorange.haohsing.databinding.ListitemShareMemberBinding

private val ShareMemberDiffUtil = object : DiffUtil.ItemCallback<UiShareMember>() {
    override fun areItemsTheSame(oldItem: UiShareMember, newItem: UiShareMember): Boolean {
        return oldItem.shareMember.userId == newItem.shareMember.userId
    }

    override fun areContentsTheSame(oldItem: UiShareMember, newItem: UiShareMember): Boolean {
        return oldItem == newItem
    }
}

class ShareMemberAdapter(private val listener: Listener) :
    ListAdapter<UiShareMember, ShareMemberAdapter.ViewHolder>(ShareMemberDiffUtil) {

    interface Listener {
        fun onDeleteMemberClick(member: ShareMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemShareMemberBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemShareMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btDeleteMember.setOnClickListener {
                listener.onDeleteMemberClick(getItem(bindingAdapterPosition).shareMember)
            }
        }

        fun bind(item: UiShareMember) {
            binding.tvMail.text = item.shareMember.userMail
            binding.tvName.text =
                if (item.shareMember.isAccept) item.shareMember.userName else "待接受邀請"
            binding.tvItemCount.text = "成員${bindingAdapterPosition}"
            binding.viewDivider.isVisible = !item.isLastItem

        }
    }
}