package com.clockworkorange.haohsing.ui.main.user.devicedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.entity.DeviceSetting
import com.clockworkorange.haohsing.databinding.LayoutSwitchItemBinding

private val DeviceSettingDiffUtil = object : DiffUtil.ItemCallback<DeviceSetting>() {
    override fun areItemsTheSame(oldItem: DeviceSetting, newItem: DeviceSetting): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DeviceSetting, newItem: DeviceSetting): Boolean {
        return oldItem == newItem
    }
}

class DeviceSettingAdapter(private val listener: Listener) :
    ListAdapter<DeviceSetting, DeviceSettingAdapter.ViewHolder>(DeviceSettingDiffUtil) {

    interface Listener {
        fun onSettingChange(code: String, enable: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutSwitchItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: LayoutSwitchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val onCheckListener: CompoundButton.OnCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val item = getItem(bindingAdapterPosition)
                listener.onSettingChange(item.code, isChecked)
            }

        fun bind(item: DeviceSetting) {
            binding.tvTitle.text = item.name
            binding.swEnable.setOnCheckedChangeListener(null)
            binding.swEnable.isChecked = item.enabled
            binding.swEnable.setOnCheckedChangeListener(onCheckListener)
        }

    }
}