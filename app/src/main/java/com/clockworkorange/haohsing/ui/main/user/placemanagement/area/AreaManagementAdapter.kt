package com.clockworkorange.haohsing.ui.main.user.placemanagement.area

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.haohsing.databinding.ListitemAreaBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener

class AreaManagementAdapter(private val listener: Listener) :
    ListAdapter<AreaInfo, AreaManagementAdapter.ViewHolder>(
        AreaInfoDiffUtil
    ) {

    interface Listener : GenericAdapterListener<AreaInfo> {
        fun onDeleteClick(item: AreaInfo)
    }

    companion object {
        private val AreaInfoDiffUtil = object : DiffUtil.ItemCallback<AreaInfo>() {
            override fun areItemsTheSame(oldItem: AreaInfo, newItem: AreaInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AreaInfo, newItem: AreaInfo): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemAreaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemAreaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }

            binding.btDelete.setOnClickListener {
                listener.onDeleteClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: AreaInfo) {
            binding.tvTitle.text = item.name
        }

    }
}