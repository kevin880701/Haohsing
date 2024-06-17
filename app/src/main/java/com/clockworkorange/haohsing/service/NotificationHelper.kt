package com.clockworkorange.haohsing.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.ui.launcher.LauncherActivity
import com.clockworkorange.haohsing.ui.main.engineer.EngineerMainActivity
import com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail.TaskDetailFragmentArgs
import com.clockworkorange.haohsing.ui.main.engineer.main.taskdetailinstall.TaskDetailInstallFragmentArgs
import com.clockworkorange.haohsing.ui.main.user.MainActivity
import com.clockworkorange.haohsing.ui.main.user.devicedetail.DeviceDetailFragmentArgs
import com.clockworkorange.haohsing.utils.NotificationManager
import kotlin.random.Random

object NotificationHelper {

    private const val ChannelId = "haohsing"
    private const val ChannelName = "haohsing"

    fun showPlainNotification(context: Context, title: String, message: String) {

        createNotificationChannel(context)

        val launcherIntent = Intent(context, LauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, launcherIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ChannelId)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(notifyPendingIntent)
            .setSmallIcon(R.drawable.ic_logo_water)
            .setAutoCancel(true)
            .build()

        context.NotificationManager.notify(Random.nextInt(), notification)
    }

    fun showOpenDeviceDetailNotification(
        context: Context,
        title: String,
        message: String,
        deviceId: Int
    ) {
        createNotificationChannel(context)

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_user)
            .setDestination(R.id.deviceDetailFragment)
            .setArguments(DeviceDetailFragmentArgs(intArrayOf(deviceId), deviceId).toBundle())
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, ChannelId)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_logo_water)
            .setAutoCancel(true)
            .build()

        context.NotificationManager.notify(Random.nextInt(), notification)
    }

    fun showOpenTaskDetailNotification(
        context: Context,
        title: String,
        message: String,
        taskId: Int,
        requirement: WorkOrderRequirement
    ) {
        createNotificationChannel(context)

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(EngineerMainActivity::class.java)
            .setGraph(R.navigation.main_engineer)
            .setDestination(if (requirement == WorkOrderRequirement.Install) R.id.taskDetailInstallFragment else R.id.taskDetailFragment)
            .setArguments(
                if (requirement == WorkOrderRequirement.Install) TaskDetailInstallFragmentArgs(
                    taskId
                ).toBundle() else TaskDetailFragmentArgs(taskId).toBundle()
            )
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, ChannelId)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_logo_water)
            .setAutoCancel(true)
            .build()

        context.NotificationManager.notify(Random.nextInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        val manager = context.NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannelCompat.Builder(ChannelId, NotificationManager.IMPORTANCE_DEFAULT)
                    .setName(ChannelName)
                    .setDescription("通知")
                    .build()

            manager.createNotificationChannel(channel)
        }
    }
}