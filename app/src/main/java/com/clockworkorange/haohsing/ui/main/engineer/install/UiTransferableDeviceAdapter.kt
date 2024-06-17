package com.clockworkorange.haohsing.ui.main.engineer.install

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ListitemTransferableDeviceBinding
import com.clockworkorange.haohsing.databinding.ListitemTrasnferDeviceDateHeaderBinding
import timber.log.Timber
import java.time.format.DateTimeFormatter

private val UiInstallRecordDiffUtil = object : DiffUtil.ItemCallback<UiDevice>() {

    override fun areItemsTheSame(oldItem: UiDevice, newItem: UiDevice): Boolean {
        return oldItem.device.id == newItem.device.id
    }

    override fun areContentsTheSame(oldItem: UiDevice, newItem: UiDevice): Boolean {
        return oldItem == newItem
    }
}

private const val Type_Header = 0
private const val Type_Normal = 1

class UiTransferableDeviceAdapter :
    ListAdapter<UiDevice , RecyclerView.ViewHolder>(UiInstallRecordDiffUtil) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).showDateHeader) Type_Header else Type_Normal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type_Header -> ViewHolderHeader(
                ListitemTrasnferDeviceDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            Type_Normal -> ViewHolder(
                ListitemTransferableDeviceBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalStateException("wrong view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Type_Header -> (holder as? ViewHolderHeader)?.bind(
                getItem(
                    position
                )
            )
            Type_Normal -> (holder as? ViewHolder)?.bind(getItem(position))
        }
    }

    inner class ViewHolderHeader(val binding: ListitemTrasnferDeviceDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UiDevice) {
            binding.tvDate.text = item.device.installDateTime?.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
            binding.ilTransferDevice.bind(item)
        }

    }

    inner class ViewHolder(val binding: ListitemTransferableDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UiDevice) {
            binding.bind(item)
        }
    }

    private fun ListitemTransferableDeviceBinding.bind(uiDevice: UiDevice) {
        flRoot.setOnClickListener {
            uiDevice.isSelected = !uiDevice.isSelected
            notifyDataSetChanged()
        }
        val device = uiDevice.device
        tvModel.text = device.modelInfo.name
        tvMac.text = device.mac
        tvName.text = device.name
        tvDate.text = device.installDateTime?.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm"))
        flRoot.background = root.context.getDrawable(
            if (uiDevice.isSelected) R.drawable.shape_r10_bad6fb
            else R.drawable.shape_r10_white
        )
        ivCheck.isVisible = uiDevice.isSelected
    }

}