package com.wsluoyj.sdaily.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** 开机恢复提醒闹钟 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.schedule(context)
        }
    }
}
