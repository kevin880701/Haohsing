package com.clockworkorange.haohsing.ui.main.pair.engineer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import com.clockworkorange.haohsing.databinding.ListitemCustomerBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener

val DiffUtil_CustomerAgency = object : DiffUtil.ItemCallback<CustomerAgency>() {
    override fun areItemsTheSame(oldItem: CustomerAgency, newItem: CustomerAgency): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CustomerAgency, newItem: CustomerAgency): Boolean {
        return oldItem == newItem
    }
}

class CustomerAdapter(val listener: GenericAdapterListener<CustomerAgency>) :
    ListAdapter<CustomerAgency, CustomerAdapter.ViewHolder>(DiffUtil_CustomerAgency) {

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

        fun bind(customerAgency: CustomerAgency) {
            binding.tvName.text = customerAgency.name
        }

    }
}