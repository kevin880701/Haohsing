package com.clockworkorange.haohsing.ui.main.engineer.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.haohsing.databinding.ListitemTaskSummaryBinding
import com.clockworkorange.haohsing.databinding.ListitemTaskSummaryDateHeaderBinding
import com.clockworkorange.haohsing.ui.widget.setErrorStyle
import com.clockworkorange.haohsing.ui.widget.setFinishStyle
import com.clockworkorange.haohsing.ui.widget.setUnFinishStyle
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import java.time.format.DateTimeFormatter

private val UiTaskSummaryDiffUtil = object : DiffUtil.ItemCallback<UiTaskSummary>() {
    override fun areItemsTheSame(oldItem: UiTaskSummary, newItem: UiTaskSummary): Boolean {
        return oldItem.taskSummary.taskId == newItem.taskSummary.taskId
    }

    override fun areContentsTheSame(oldItem: UiTaskSummary, newItem: UiTaskSummary): Boolean {
        return oldItem.showDateHeader == newItem.showDateHeader
    }
}

private const val Type_Header = 0
private const val Type_Normal = 1

class UiTaskSummaryAdapter(private val listener: GenericAdapterListener<TaskSummary>) :
    ListAdapter<UiTaskSummary, RecyclerView.ViewHolder>(UiTaskSummaryDiffUtil) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).showDateHeader) Type_Header else Type_Normal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type_Header -> ViewHolderHeader(
                ListitemTaskSummaryDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            Type_Normal -> ViewHolder(
                ListitemTaskSummaryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalStateException("wrong view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Type_Header -> (holder as? ViewHolderHeader)?.bind(getItem(position).taskSummary)
            Type_Normal -> (holder as? ViewHolder)?.bind(getItem(position).taskSummary)
        }
    }

    inner class ViewHolderHeader(val binding: ListitemTaskSummaryDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ilTaskSummary.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition).taskSummary)
            }
        }

        fun bind(item: TaskSummary) {
            binding.tvDate.text = item.dateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
            with(binding.ilTaskSummary) {
                tvAddress.text = item.address
                tvCustomer.text = item.name
                tvTaskOrder.text = "${item.requirement.displayString}-${item.taskId}"

                tvTime.text = item.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                when (item.statusCategory) {
                    TaskStatusCategory.UnFinish -> setUnFinishStyle(item)
                    TaskStatusCategory.Finish -> setFinishStyle(item.status)
                    TaskStatusCategory.Error -> setErrorStyle(item)
                }
            }
        }
    }

    inner class ViewHolder(val binding: ListitemTaskSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition).taskSummary)
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