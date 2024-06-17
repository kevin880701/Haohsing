package com.clockworkorange.haohsing.ui.main.pair.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.vendor.Vendor
import com.clockworkorange.haohsing.databinding.ListitemCustomerBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener

val DiffUtil_Vendor = object : DiffUtil.ItemCallback<Vendor>() {
    override fun areItemsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
        return oldItem == newItem
    }
}

class VendorAdapter(val listener: GenericAdapterListener<Vendor>) :
    ListAdapter<Vendor, VendorAdapter.ViewHolder>(DiffUtil_Vendor) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemCustomerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListitemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(vendor: Vendor) {
            binding.tvName.text = vendor.name
        }

    }
}