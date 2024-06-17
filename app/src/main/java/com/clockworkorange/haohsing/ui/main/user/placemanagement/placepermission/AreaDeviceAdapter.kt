package com.clockworkorange.haohsing.ui.main.user.placemanagement.placepermission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.databinding.ListitemAreaDeviceShareStatusBinding
import com.clockworkorange.haohsing.ui.widget.GridSpacingItemDecoration
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import com.clockworkorange.haohsing.utils.ScreenUtils

private val AreaDeviceDiffUtil = object : DiffUtil.ItemCallback<AreaDevice>() {
    override fun areItemsTheSame(oldItem: AreaDevice, newItem: AreaDevice): Boolean {
        return oldItem.areaInfo == newItem.areaInfo
    }

    override fun areContentsTheSame(oldItem: AreaDevice, newItem: AreaDevice): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: AreaDevice, newItem: AreaDevice): Any? {
        return if (oldItem.areaDevices.count { it.isCheck } != newItem.areaDevices.count { it.isCheck }) {
            Bundle().apply { putBoolean("checkStateChange", true) }
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}

class AreaDeviceAdapter(val listener: GenericAdapterListener<UiCheckStatusDevice>) :
    ListAdapter<AreaDevice, AreaDeviceAdapter.ViewHolder>(AreaDeviceDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemAreaDeviceShareStatusBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(getItem(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            holder.binding(item)
        } else {
            val bundle = payloads[0] as Bundle
            holder.update(bundle, item)
        }
    }

    inner class ViewHolder(val binding: ListitemAreaDeviceShareStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            val gridLayoutManager = GridLayoutManager(binding.root.context, 3)
            val decoration =
                GridSpacingItemDecoration(3, ScreenUtils.dp2px(binding.root.context, 8), false)
            binding.rvDevices.layoutManager = gridLayoutManager
            binding.rvDevices.addItemDecoration(decoration)
            binding.rvDevices.adapter = CheckStatusDeviceAdapter(listener)
            binding.rvDevices.itemAnimator = null
        }

        fun binding(item: AreaDevice) {
            binding.tvAreaName.text = item.areaInfo.name
            binding.viewDivider.isVisible = !item.isLast
            (binding.rvDevices.adapter as? CheckStatusDeviceAdapter)?.submitList(item.areaDevices)
        }

        fun update(bundle: Bundle, item: AreaDevice) {
            if (bundle.containsKey("checkStateChange")) {
                (binding.rvDevices.adapter as? CheckStatusDeviceAdapter)?.submitList(item.areaDevices)
            }
        }
    }

}