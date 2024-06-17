package com.clockworkorange.haohsing.ui.main.engineer.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.haohsing.databinding.ListitemTaskSummaryBinding
import com.clockworkorange.haohsing.ui.widget.setErrorStyle
import com.clockworkorange.haohsing.ui.widget.setFinishStyle
import com.clockworkorange.haohsing.ui.widget.setUnFinishStyle
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import java.time.format.DateTimeFormatter

private val TaskSummaryDiffUtil = object : DiffUtil.ItemCallback<TaskSummary>() {
    override fun areItemsTheSame(oldItem: TaskSummary, newItem: TaskSummary): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(oldItem: TaskSummary, newItem: TaskSummary): Boolean {
        return oldItem == newItem
    }
}

class TaskSummaryAdapter(private val listener: GenericAdapterListener<TaskSummary>) :
    ListAdapter<TaskSummary, TaskSummaryAdapter.ViewHolder>(TaskSummaryDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemTaskSummaryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemTaskSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: TaskSummary) {
            binding.tvAddress.text = item.address
            binding.tvCustomer.text = item.name
            binding.tvTaskOrder.text = "${item.requirement.displayString}-${item.taskId}"

            binding.tvTime.text = item.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

             when (item.statusCategory) {
                TaskStatusCategory.UnFinish -> binding.setUnFinishStyle(item)
                TaskStatusCategory.Finish -> binding.setFinishStyle(item.status)
                TaskStatusCategory.Error -> binding.setErrorStyle(item)
            }
        }

    }

}