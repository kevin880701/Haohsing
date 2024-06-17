package com.clockworkorange.haohsing.ui.main.user.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.domain.usecase.notification.Notification
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.ListitemNotificationBinding
import com.clockworkorange.haohsing.databinding.ListitemNotificationDateHeaderBinding
import com.clockworkorange.haohsing.utils.GenericAdapterListener
import java.time.format.DateTimeFormatter

private val UiNotificationDiffUtil =
    object : DiffUtil.ItemCallback<UserNotificationViewModel.UiNotification>() {
        override fun areItemsTheSame(
            oldItem: UserNotificationViewModel.UiNotification,
            newItem: UserNotificationViewModel.UiNotification
        ): Boolean {
            return oldItem.notification.id == newItem.notification.id
        }

        override fun areContentsTheSame(
            oldItem: UserNotificationViewModel.UiNotification,
            newItem: UserNotificationViewModel.UiNotification
        ): Boolean {
            return oldItem.notification == newItem.notification
        }
    }

private const val Type_Header = 0
private const val Type_Normal = 1

class NotificationAdapter(private val listener: GenericAdapterListener<UserNotificationViewModel.UiNotification>) :
    ListAdapter<UserNotificationViewModel.UiNotification, RecyclerView.ViewHolder>(
        UiNotificationDiffUtil
    ) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).showDateHeader) Type_Header else Type_Normal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type_Header -> ViewHolderHeader(
                ListitemNotificationDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            Type_Normal -> ViewHolder(
                ListitemNotificationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Type_Header -> (holder as? ViewHolderHeader)?.bind(getItem(position))
            Type_Normal -> (holder as? ViewHolder)?.bind(getItem(position))
        }
    }

    inner class ViewHolderHeader(val binding: ListitemNotificationDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ilNotification.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: UserNotificationViewModel.UiNotification) {
            binding.tvDate.text =
                item.notification.time.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
            binding.ilNotification
            binding.ilNotification.bind(item)
        }
    }

    inner class ViewHolder(val binding: ListitemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: UserNotificationViewModel.UiNotification) {
            binding.bind(item)
        }
    }

    fun ListitemNotificationBinding.bind(item: UserNotificationViewModel.UiNotification) {
        ivUnread.isVisible = !item.notification.isRead

        when (item.notification.type) {
            Notification.Type.Alert -> {
                ivIconBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.color_4dff7974
                    )
                )
                ivIcon.setImageResource(R.drawable.ic_privacy)
                ivIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.colorRed))
                ivArrow.isVisible = true
            }
            Notification.Type.DataAnalysis -> {
                ivIconBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.color_f1f1f1
                    )
                )
                ivIcon.setImageResource(R.drawable.ic_lightning)
                ivIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.colorSecondary))
                ivArrow.isVisible = true
            }
            Notification.Type.Maintenance -> {
                ivIconBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.color_f1f1f1
                    )
                )
                ivIcon.setImageResource(R.drawable.ic_wrench)
                ivIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.colorSecondary))
                ivArrow.isVisible = true
            }
            Notification.Type.TaskDelay -> {
                ivIconBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.color_4dff7974
                    )
                )
                ivIcon.setImageResource(R.drawable.ic_wrench)
                ivIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.colorRed))
                ivArrow.isVisible = true
            }
            Notification.Type.UnKnown -> {}
        }

        tvTitle.text = item.notification.title
        tvContent.text =
            "${item.notification.time.format(DateTimeFormatter.ofPattern("HH:mm"))} | ${item.notification.message}"
    }

}