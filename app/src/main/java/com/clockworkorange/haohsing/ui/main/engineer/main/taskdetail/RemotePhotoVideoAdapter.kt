package com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.clockworkorange.haohsing.databinding.ListitemRemotePhotoVideoBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener

private val StringDiffUtil = object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return newItem == oldItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return newItem == oldItem
    }
}

class RemotePhotoVideoAdapter(private val listener: GenericAdapterListener<String>) :
    ListAdapter<String, RemotePhotoVideoAdapter.ViewHolder>(StringDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemRemotePhotoVideoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemRemotePhotoVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { listener.onItemClick(getItem(bindingAdapterPosition)) }
        }

        fun bind(url: String) {

            val loadingDrawable = CircularProgressDrawable(binding.root.context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            }

            Glide.with(binding.ivCover)
                .load(url)
                .placeholder(loadingDrawable)
                .into(binding.ivCover)

            if (url.endsWith("mp4")) {
                binding.tvVideoDuration.text = "video"
            }

        }
    }
}