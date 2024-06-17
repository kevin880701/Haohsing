package com.clockworkorange.haohsing.ui.main.user.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.entity.PowerSchedule
import com.clockworkorange.domain.entity.ScheduleType
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.LayoutInfoItemBinding
import com.clockworkorange.haohsing.ui.widget.setTextAndArrow
import com.clockworkorange.haohsing.ui.widget.setTextOnly
import com.clockworkorange.haohsing.utils.GenericAdapterListener

private val PowerScheduleDiffUtil = object : DiffUtil.ItemCallback<PowerSchedule>() {
    override fun areItemsTheSame(oldItem: PowerSchedule, newItem: PowerSchedule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PowerSchedule, newItem: PowerSchedule): Boolean {
        return oldItem == newItem
    }
}

class ScheduleAdapter(private val listener: GenericAdapterListener<PowerSchedule>, private val pageScope: ScheduleType) :
    ListAdapter<PowerSchedule, ScheduleAdapter.ViewHolder>(PowerScheduleDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInfoItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: LayoutInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: PowerSchedule) {
            binding.run {
                if (item.getScheduleType() == ScheduleType.Area && pageScope == ScheduleType.Device) {
                    setTextOnly(item.name, "群控排程")
                    tvInfo.setTextColor(binding.root.context.getColor(R.color.colorLightText))
                } else {
                    setTextAndArrow(item.name)
                }
            }
        }
    }
}