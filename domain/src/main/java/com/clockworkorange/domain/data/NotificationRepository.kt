package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.local.AppDatabase
import com.clockworkorange.domain.data.local.ReadMessageId
import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.usecase.notification.Invite
import com.clockworkorange.domain.usecase.notification.Notification
import com.clockworkorange.domain.usecase.notification.Duration
import kotlinx.coroutines.flow.first

interface NotificationRepository {
    suspend fun getInviteList(): List<Invite>
    suspend fun getNotificationList(duration: Duration): List<Notification>
    suspend fun markAllNotificationAsRead(): Boolean
    suspend fun replyInvite(inviteId: Int, accept: Boolean): Boolean
}

class NotificationRepositoryImpl(
    private val service: ServiceWrapper,
    private val appDatabase: AppDatabase
) : NotificationRepository {

    override suspend fun getInviteList(): List<Invite> {
        return service.getInviteList().map { it.toInvite() }
    }

    override suspend fun getNotificationList(duration: Duration): List<Notification> {
        val readIds = appDatabase.readMessageIdDao().getAllReadIds().first().map { it.messageId }
        return service.getMsgList().mapNotNull { it.toNotification(readIds) }
    }

    override suspend fun markAllNotificationAsRead(): Boolean {
        val ids = service.getMsgList().map { it.msgId }
        ids.forEach { id ->
            appDatabase.readMessageIdDao().insert(ReadMessageId(id))
        }
        return true
    }

    override suspend fun replyInvite(inviteId: Int, accept: Boolean): Boolean {
        return service.acceptOrRejectInvite(inviteId, accept)
    }
}