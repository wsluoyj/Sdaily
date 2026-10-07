package com.wsluoyj.sdaily.data.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wsluoyj.sdaily.MainActivity
import com.wsluoyj.sdaily.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                ReminderScheduler.CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
                .setName("记录提醒")
                .setDescription("每日定时提醒记录")
                .build()
        )

        val contentPi = PendingIntent.getActivity(
            context,
            3001,
            Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("记录提醒")
            .setContentText("今晚记得打开 私迹 记录一下")
            .setAutoCancel(true)
            .setContentIntent(contentPi)
            .build()
        try {
            manager.notify(1001, notification)
        } catch (e: SecurityException) {
            // 用户未授予通知权限时静默跳过
        }

        // 明天同一时间再响
        ReminderScheduler.schedule(context)
    }
}
