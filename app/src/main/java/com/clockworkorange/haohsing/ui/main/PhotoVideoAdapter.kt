package com.clockworkorange.haohsing.ui.main

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clockworkorange.haohsing.databinding.ListitemPhotoVideoBinding

private val UriDiffUtil = object : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }
}

class PhotoVideoAdapter(
    private val enableRemoveButton: Boolean = true,
    var listener: Listener? = null
) : ListAdapter<Uri, PhotoVideoAdapter.ViewHolder>(UriDiffUtil) {

    fun interface Listener {
        fun onRemoveItem(itemUri: Uri)
        fun onItemClick(itemUri: Uri) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemPhotoVideoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListitemPhotoVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivClose.isVisible = enableRemoveButton
            binding.ivClose.setOnClickListener {
                listener?.onRemoveItem(getItem(bindingAdapterPosition))
            }

            binding.root.setOnClickListener { listener?.onRemoveItem(getItem(bindingAdapterPosition)) }
        }

        fun bind(itemUri: Uri) {
            Glide.with(binding.ivCover)
                .load(itemUri)
                .into(binding.ivCover)

            if (itemUri.toString().contains("video")) {

                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(binding.root.context, itemUri)
                val durationMs =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull()
                mediaMetadataRetriever.release()
                if (durationMs == null) {
                    return
                }

                val durationSec = durationMs / 1000

                binding.tvVideoDuration.text =
                    String.format("%02d:%02d", (durationSec % 3600) / 60, (durationSec % 60));
            } else {
                binding.tvVideoDuration.text = ""
            }
        }

    }
}