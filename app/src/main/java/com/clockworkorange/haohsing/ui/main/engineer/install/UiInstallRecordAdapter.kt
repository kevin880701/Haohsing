package com.clockworkorange.haohsing.ui.main.engineer.install

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.task.InstallRecord
import com.clockworkorange.haohsing.databinding.ListitemInstallRecordBinding
import com.clockworkorange.haohsing.databinding.ListitemInstallRecordDateHeaderBinding
import java.time.format.DateTimeFormatter

private val UiInstallRecordDiffUtil = object : DiffUtil.ItemCallback<UiInstallRecord>() {

    override fun areItemsTheSame(oldItem: UiInstallRecord, newItem: UiInstallRecord): Boolean {
        return oldItem.installRecord.mac == newItem.installRecord.mac
    }

    override fun areContentsTheSame(oldItem: UiInstallRecord, newItem: UiInstallRecord): Boolean {
        return oldItem.showDateHeader == newItem.showDateHeader
    }
}

private const val Type_Header = 0
private const val Type_Normal = 1

class UiInstallRecordAdapter :
    ListAdapter<UiInstallRecord, RecyclerView.ViewHolder>(UiInstallRecordDiffUtil) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).showDateHeader) Type_Header else Type_Normal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type_Header -> ViewHolderHeader(
                ListitemInstallRecordDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            Type_Normal -> ViewHolder(
                ListitemInstallRecordBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalStateException("wrong view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Type_Header -> (holder as? UiInstallRecordAdapter.ViewHolderHeader)?.bind(
                getItem(
                    position
                )
            )
            Type_Normal -> (holder as? UiInstallRecordAdapter.ViewHolder)?.bind(getItem(position))
        }
    }

    inner class ViewHolderHeader(val binding: ListitemInstallRecordDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UiInstallRecord) {
            binding.tvDate.text = item.installRecord.date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
            binding.ilInstallRecord.bind(item.installRecord)
        }

    }

    inner class ViewHolder(val binding: ListitemInstallRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UiInstallRecord) {
            binding.bind(item.installRecord)
        }
    }

    private fun ListitemInstallRecordBinding.bind(record: InstallRecord) {
        tvModel.text = record.model
        tvMac.text = record.mac
        tvPlace.text = record.place
        tvDate.text = record.date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日\nHH:mm"))
    }
}