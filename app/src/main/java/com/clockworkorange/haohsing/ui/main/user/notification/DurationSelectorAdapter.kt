package com.clockworkorange.haohsing.ui.main.user.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.haohsing.databinding.ListitemDurationSelectorBinding

class DurationSelectorAdapter(private val listener: Listener) :
    RecyclerView.Adapter<DurationSelectorAdapter.ViewHolder>() {

    interface Listener {
        fun onDurationSelectorClick()
    }

    var duration: Duration = Duration.Recent1Month
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemDurationSelectorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(duration)
    }

    inner class ViewHolder(val binding: ListitemDurationSelectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { listener.onDurationSelectorClick() }
        }

        fun bind(duration: Duration) {
            binding.tvDuration.text = when (duration) {
                Duration.Recent1Month -> "近一個月"
                Duration.Recent2Month -> "近兩個月"
                Duration.Recent3Month -> "近三個月"
            }
        }

    }
}