package com.clockworkorange.haohsing.utils

import android.content.Context
import androidx.core.app.NotificationManagerCompat

val Context.NotificationManager
    get() = NotificationManagerCompat.from(this)