package com.clockworkorange.haohsing.ui.main.user.devicedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.entity.FilterStatus
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.LayoutInfoItemBinding
import com.clockworkorange.haohsing.utils.Filter

private val FilterStatusDiffUtil = object : DiffUtil.ItemCallback<FilterStatus>() {
    override fun areItemsTheSame(oldItem: FilterStatus, newItem: FilterStatus): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: FilterStatus, newItem: FilterStatus): Boolean {
        return oldItem == newItem
    }
}

class FilterAdapter : ListAdapter<FilterStatus, FilterAdapter.ViewHolder>(FilterStatusDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: LayoutInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilterStatus) {
            binding.tvTitle.text = "${item.name} 壽命剩餘時間"
            binding.ivArrow.isVisible = false
            binding.tvInfo.isVisible = true
            if (Filter.needChange(item.health)) {
                binding.tvInfo.text = "${item.health}% 需更換"
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorRed
                    )
                )
            } else {
                binding.tvInfo.text = "${item.health}%"
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGray
                    )
                )
            }
        }
    }

}