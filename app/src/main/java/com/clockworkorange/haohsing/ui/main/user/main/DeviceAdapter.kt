package com.clockworkorange.haohsing.ui.main.user.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.entity.DeviceStatus
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ListitemDevicesBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.loadImage

class DeviceAdapter(val listener: GenericAdapterListener<Device>) :
    ListAdapter<Device, DeviceAdapter.ViewHolder>(DeviceDiffUtil) {

    companion object {
        val DeviceDiffUtil = object : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemDevicesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemDevicesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: Device) {
            with(binding) {
                tvName.text = item.name
                binding.ivDevice.loadImage(item.modelInfo.imageUrl)
                if (item.isIoTDevice) {
                    when (item.status) {
                        is DeviceStatus.Error -> setStatusError()
                        DeviceStatus.Loading -> setStatusLoading()
                        DeviceStatus.Normal -> setStatusNormal()
                        DeviceStatus.Offline -> setStatusOffline()
                        DeviceStatus.Off -> setStatusOff()
                    }
                } else {
                    setStatusNone()
                }
            }
        }

        private fun setStatusNormal() {
            binding.ivStatus.isVisible = true
            binding.ivStatus.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.select_green1
                )
            )
            binding.tvStatus.isVisible = false
            binding.tvName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorSecondary
                )
            )
            binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))

        }

        private fun setStatusOff() {
            binding.ivStatus.isVisible = true
            binding.ivStatus.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLightGray
                )
            )
            binding.tvStatus.isVisible = false
            binding.tvName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorSecondary
                )
            )
            binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))

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
            binding.tvName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorSecondary
                )
            )
            binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))

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
            binding.tvName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGray
                )
            )
            binding.root.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLightGray
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
            binding.tvName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorSecondary
                )
            )
            binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))

        }

        private fun setStatusNone() {
            binding.ivStatus.isVisible = false
            binding.tvStatus.isVisible = false
            binding.tvName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorSecondary
                )
            )
            binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))

        }
    }
}