package com.clockworkorange.haohsing.ui.main.user.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.databinding.ListitemInviteHeaderBinding

class InviteHeaderAdapter : RecyclerView.Adapter<InviteHeaderAdapter.ViewHolder>() {

    var isVisible: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = if (isVisible) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemInviteHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    inner class ViewHolder(val binding: ListitemInviteHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

}