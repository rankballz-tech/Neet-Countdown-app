package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.model.NeetConstants
import com.example.notification.NeetNotificationHelper
import com.example.widget.NeetCountdownWidgetProvider

class NeetBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
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

            // Restore background periodic widget updates and refresh all widgets immediately
            NeetNotificationHelper.scheduleWidgetPeriodicUpdates(context)
            NeetCountdownWidgetProvider.updateAllWidgets(context)
            com.example.service.NeetLiveWidgetService.start(context)

            // Restore ongoing background countdown notification if enabled
            if (NeetNotificationHelper.isOngoingNotificationEnabled(context)) {
                NeetNotificationHelper.showOngoingCountdownNotification(context)
            }
        }
    }
}
