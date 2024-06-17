package com.clockworkorange.haohsing.ui.main.user.placemanagement.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.palcearea.PlaceSummary
import com.clockworkorange.haohsing.databinding.ListitemPlaceBinding

class PlaceManagementAdapter(private val listener: Listener) :
    ListAdapter<PlaceSummary, PlaceManagementAdapter.ViewHolder>(
        PlaceInfoDiffUtil
    ) {

    interface Listener {
        fun onPlaceNameClick(placeInfo: PlaceSummary)
        fun onPlaceAreaClick(placeInfo: PlaceSummary)
        fun onPlaceShareClick(placeInfo: PlaceSummary)
        fun onDeletePlaceClick(placeInfo: PlaceSummary)
    }

    companion object {
        val PlaceInfoDiffUtil = object : DiffUtil.ItemCallback<PlaceSummary>() {
            override fun areItemsTheSame(oldItem: PlaceSummary, newItem: PlaceSummary): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: PlaceSummary, newItem: PlaceSummary): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.flPlaceName.setOnClickListener {
                listener.onPlaceNameClick(
                    getItem(
                        bindingAdapterPosition
                    )
                )
            }
            binding.flAreaCount.setOnClickListener {
                listener.onPlaceAreaClick(
                    getItem(
                        bindingAdapterPosition
                    )
                )
            }
            binding.flShareCount.setOnClickListener {
                listener.onPlaceShareClick(
                    getItem(
                        bindingAdapterPosition
                    )
                )
            }
            binding.btDeletePlace.setOnClickListener {
                listener.onDeletePlaceClick(
                    getItem(
                        bindingAdapterPosition
                    )
                )
            }
        }

        fun bind(item: PlaceSummary) {
            binding.tvPlaceCount.text = "單位$bindingAdapterPosition"
            binding.tvPlaceName.text = item.name
            binding.tvAreaCount.text = "%d個區域".format(item.areaCount)
            binding.tvShareCount.text = "%d個成員".format(item.shareCount)
        }

    }
}