package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.model.NeetConstants
import com.example.notification.NeetNotificationHelper
import com.example.widget.NeetCountdownWidgetProvider

class NeetBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            // Restore channel and schedule all milestones
            NeetNotificationHelper.createNotificationChannel(context)
            NeetNotificationHelper.scheduleAllMilestones(context)

            // Restore daily reminder if enabled
            val prefs = context.getSharedPreferences(NeetConstants.PREFS_NAME, Context.MODE_PRIVATE)
            val isDailyEnabled = prefs.getBoolean(NeetConstants.KEY_DAILY_REMINDER_ENABLED, true)
            if (isDailyEnabled) {
                val hour = prefs.getInt(NeetConstants.KEY_DAILY_REMINDER_HOUR, 8)
                val minute = prefs.getInt(NeetConstants.KEY_DAILY_REMINDER_MINUTE, 0)
                NeetNotificationHelper.scheduleDailyReminder(context, hour, minute)
            }

            // Refresh home screen widget
            NeetCountdownWidgetProvider.updateAllWidgets(context)
        }
    }
}
