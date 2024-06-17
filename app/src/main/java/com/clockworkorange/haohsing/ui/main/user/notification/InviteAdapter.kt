package com.clockworkorange.haohsing.ui.main.user.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.databinding.ListitemInviteBinding

private val UiInviteDiffUtil =
    object : DiffUtil.ItemCallback<UserNotificationViewModel.UiInvite>() {
        override fun areItemsTheSame(
            oldItem: UserNotificationViewModel.UiInvite,
            newItem: UserNotificationViewModel.UiInvite
        ): Boolean {
            return oldItem.invite.id == newItem.invite.id
        }

        override fun areContentsTheSame(
            oldItem: UserNotificationViewModel.UiInvite,
            newItem: UserNotificationViewModel.UiInvite
        ): Boolean {
            return oldItem.invite == newItem.invite
        }
    }

class InviteAdapter :
    ListAdapter<UserNotificationViewModel.UiInvite, InviteAdapter.ViewHolder>(UiInviteDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemInviteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemInviteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uiInvite: UserNotificationViewModel.UiInvite) {
            binding.tvSender.text = uiInvite.invite.senderName
            binding.tvTitle.text = "邀請您使用飲水機"
            binding.tvContent.text = uiInvite.invite.placeName ?: ""
            binding.btReject.setOnClickListener { uiInvite.rejectAction.invoke() }
            binding.btAccept.setOnClickListener { uiInvite.acceptAction.invoke() }
        }

    }
}