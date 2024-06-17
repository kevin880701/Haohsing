package com.clockworkorange.haohsing.service

import com.clockworkorange.domain.data
import com.clockworkorange.domain.di.ApplicationScope
import com.clockworkorange.domain.usecase.task.GetTaskDetailUseCase
import com.clockworkorange.domain.usecase.user.UpLoadFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var upLoadFcmTokenUseCase: UpLoadFcmTokenUseCase

    @Inject
    lateinit var getTaskDetailUseCase: GetTaskDetailUseCase


    override fun onNewToken(token: String) {
        applicationScope.launch {
            upLoadFcmTokenUseCase.invoke(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("onMessageReceived data payload ${remoteMessage.data}")

        //message.data
        //0 = "body = #56 保養飲水機"
        //1 = "m_id = 56"
        //2 = "device_id = 0"
        //3 = "title = 新派工成立"
        val keys = remoteMessage.data.keys
        if (keys.containsAll(listOf("title", "body", "m_id", "device_id"))) {
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["body"]
            val taskId = remoteMessage.data["m_id"]?.toInt()
            val deviceId = remoteMessage.data["device_id"]?.toInt()
            title ?: return
            message ?: return
            if (taskId == null && deviceId == null) {
                NotificationHelper.showPlainNotification(applicationContext, title, message)
            } else if (deviceId != null && deviceId != 0) {
                NotificationHelper.showOpenDeviceDetailNotification(
                    applicationContext,
                    title,
                    message,
                    deviceId
                )
            } else if (taskId != null && taskId != 0) {
                applicationScope.launch {
                    getTaskDetailUseCase(taskId).data?.let {
                        NotificationHelper.showOpenTaskDetailNotification(
                            applicationContext,
                            title,
                            message,
                            taskId,
                            it.requirement
                        )
                    }
                }

            }
            Timber.d("title: $title, message:$message taskId:$taskId, deviceId:$deviceId")
        }
    }


}