package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.workorder.WorkOrderInfo
import com.clockworkorange.domain.usecase.workorder.WorkOrderStatus
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ListitemWorkOrderInfoBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

private val WorkOrderInfoDiffUtil = object : DiffUtil.ItemCallback<WorkOrderInfo>() {
    override fun areItemsTheSame(oldItem: WorkOrderInfo, newItem: WorkOrderInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkOrderInfo, newItem: WorkOrderInfo): Boolean {
        return oldItem == newItem
    }
}

class WorkOrderInfoAdapter(private val listener: GenericAdapterListener<WorkOrderInfo>) :
    ListAdapter<WorkOrderInfo, WorkOrderInfoAdapter.ViewHolder>(WorkOrderInfoDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemWorkOrderInfoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemWorkOrderInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val sdf = SimpleDateFormat("yyyy年MM月dd日")

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: WorkOrderInfo) {

            binding.tvType.text = item.requirement.displayString

            binding.tvDate.text = item.date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))

            when (item.status) {
                WorkOrderStatus.Pending -> {
                    binding.tvState.isVisible = true
                    binding.tvState.text = "待處理"
                    binding.tvState.setBackgroundResource(R.drawable.shape_round_bad6fb)
                    binding.tvState.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorPrimary
                        )
                    )
                }
                WorkOrderStatus.Schedule -> {
                    binding.tvState.isVisible = true
                    binding.tvState.text = "已安排"
                    binding.tvState.setBackgroundResource(R.drawable.shape_round_bad6fb)
                    binding.tvState.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorPrimary
                        )
                    )
                }
                WorkOrderStatus.Finished -> {
                    binding.tvState.isVisible = false
                }
                WorkOrderStatus.Cancel -> {
                    binding.tvState.isVisible = true
                    binding.tvState.text = "已取消"
                    binding.tvState.setBackgroundResource(R.drawable.shape_round_acacac)
                    binding.tvState.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.white
                        )
                    )
                }
            }

        }

    }
}