package com.clockworkorange.haohsing.ui.main.pair.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ListitemWifiBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener

class SelectWiFiAdapter(private val listener: GenericAdapterListener<SimpleWiFiInfo>) :
    RecyclerView.Adapter<SelectWiFiAdapter.ViewHolder>() {

    val data = mutableListOf<SimpleWiFiInfo>()

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemWifiBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val binding: ListitemWifiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(data[bindingAdapterPosition])
            }
        }

        fun bind(simpleWiFiInfo: SimpleWiFiInfo) {
            with(binding) {
                tvName.text = simpleWiFiInfo.ssid
                val signalRes = when (simpleWiFiInfo.getSignalLevel()) {
                    SimpleWiFiInfo.SignalLevel.Leve1 -> R.drawable.ic_wifi_1
                    SimpleWiFiInfo.SignalLevel.Leve2 -> R.drawable.ic_wifi_2
                    SimpleWiFiInfo.SignalLevel.Leve3 -> R.drawable.ic_wifi_3
                    SimpleWiFiInfo.SignalLevel.Leve4 -> R.drawable.ic_wifi_4
                }
                ivSignal.setImageResource(signalRes)
            }

        }

    }

}