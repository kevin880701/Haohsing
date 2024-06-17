package com.clockworkorange.haohsing.ui.main.user.placemanagement.placesharemember

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.databinding.ListitemPlaceShareMemberBinding

private val UiPlaceShareMemberDiffUtil = object : DiffUtil.ItemCallback<UiPlaceShareMember>() {
    override fun areItemsTheSame(
        oldItem: UiPlaceShareMember,
        newItem: UiPlaceShareMember
    ): Boolean {
        return oldItem.placeShareMember.member.userId == newItem.placeShareMember.member.userId
    }

    override fun areContentsTheSame(
        oldItem: UiPlaceShareMember,
        newItem: UiPlaceShareMember
    ): Boolean {
        return oldItem == newItem
    }
}

class PlaceDeviceShareMemberAdapter(private val listener: Listener) :
    ListAdapter<UiPlaceShareMember, PlaceDeviceShareMemberAdapter.ViewHolder>(
        UiPlaceShareMemberDiffUtil
    ) {

    interface Listener {
        fun onDeletePlaceDeviceShareMember(member: UiPlaceShareMember)
        fun onMemberShareDeviceClick(member: UiPlaceShareMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemPlaceShareMemberBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemPlaceShareMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btDeleteMember.setOnClickListener {
                listener.onDeletePlaceDeviceShareMember(getItem(bindingAdapterPosition))
            }

            binding.flShareDeviceCount.setOnClickListener {
                listener.onMemberShareDeviceClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: UiPlaceShareMember) {
            binding.tvItemCount.text = "成員${bindingAdapterPosition + 1}"
            binding.tvName.text = item.placeShareMember.member.userName
            binding.tvMail.text = item.placeShareMember.member.userMail
            binding.tvDeviceCount.text = "${item.placeShareMember.deviceCount}台飲水機"
            binding.viewDivider.isVisible = !item.isLast
        }

    }
}