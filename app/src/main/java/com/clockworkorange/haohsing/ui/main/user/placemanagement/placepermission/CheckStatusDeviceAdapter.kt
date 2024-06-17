package com.clockworkorange.haohsing.ui.main.user.placemanagement.placepermission

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.entity.DeviceStatus
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ListitemCheckableDeviceBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener

private val UiCheckStatusDeviceDiffUtil = object : DiffUtil.ItemCallback<UiCheckStatusDevice>() {
    override fun areItemsTheSame(
        oldItem: UiCheckStatusDevice,
        newItem: UiCheckStatusDevice
    ): Boolean {
        return oldItem.device.id == newItem.device.id
    }

    override fun areContentsTheSame(
        oldItem: UiCheckStatusDevice,
        newItem: UiCheckStatusDevice
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: UiCheckStatusDevice,
        newItem: UiCheckStatusDevice
    ): Any? {
        return if (oldItem.isCheck != newItem.isCheck) {
            val diff = Bundle()
            diff.putBoolean("isCheck", newItem.isCheck)
            diff
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}

class CheckStatusDeviceAdapter(val listener: GenericAdapterListener<UiCheckStatusDevice>) :
    ListAdapter<UiCheckStatusDevice, CheckStatusDeviceAdapter.ViewHolder>(
        UiCheckStatusDeviceDiffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemCheckableDeviceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)

        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            holder.bind(item)
        } else {
            val bundle = payloads[0] as Bundle
            holder.update(bundle)
        }
    }

    inner class ViewHolder(val binding: ListitemCheckableDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: UiCheckStatusDevice) {
            binding.tvName.text = item.device.name
            when (item.device.status) {
                is DeviceStatus.Error -> setStatusError()
                DeviceStatus.Loading -> setStatusLoading()
                DeviceStatus.Normal -> setStatusNormal()
                DeviceStatus.Offline -> setStatusOffline()
            }

            binding.ivCheckStatus.setImageResource(
                if (item.isCheck) R.drawable.ic_check_2 else R.drawable.ic_uncheck_2
            )
        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey("isCheck")) {
                val newCheckStatus = bundle.getBoolean("isCheck")
                binding.ivCheckStatus.setImageResource(
                    if (newCheckStatus) R.drawable.ic_check_2 else R.drawable.ic_uncheck_2
                )
            }
        }

        private fun setStatusNormal() {
            binding.ivStatus.isVisible = true
            binding.tvStatus.isVisible = false
        }

        private fun setStatusError() {
            binding.ivStatus.isVisible = false
            binding.tvStatus.isVisible = true
            binding.tvStatus.text = "異常"
            binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorRed
                )
            )
        }

        private fun setStatusOffline() {
            binding.ivStatus.isVisible = false
            binding.tvStatus.isVisible = true
            binding.tvStatus.text = "未連線"
            binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.color_gray_808080
                )
            )

        }

        private fun setStatusLoading() {
            binding.ivStatus.isVisible = false
            binding.tvStatus.isVisible = true
            binding.tvStatus.text = "連線中"
            binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.color_gray_808080
                )
            )
        }

    }
}