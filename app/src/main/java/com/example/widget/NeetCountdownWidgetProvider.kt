package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.model.CountdownState
import com.example.model.NeetConstants

class NeetCountdownWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        com.example.notification.NeetNotificationHelper.scheduleWidgetPeriodicUpdates(context)
        updateAllWidgets(context)
        com.example.service.NeetLiveWidgetService.start(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        com.example.notification.NeetNotificationHelper.cancelWidgetPeriodicUpdates(context)
        com.example.service.NeetLiveWidgetService.stop(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        com.example.notification.NeetNotificationHelper.scheduleWidgetPeriodicUpdates(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        com.example.service.NeetLiveWidgetService.start(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action == NeetConstants.ACTION_REFRESH_WIDGET ||
            action == NeetConstants.ACTION_UPDATE_WIDGET ||
            action == Intent.ACTION_TIME_TICK ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            updateAllWidgets(context)
            com.example.notification.NeetNotificationHelper.scheduleWidgetPeriodicUpdates(context)
            com.example.service.NeetLiveWidgetService.start(context)

            if (action == NeetConstants.ACTION_REFRESH_WIDGET) {
                val state = CountdownState.calculate(isIstMode = true)
                android.widget.Toast.makeText(
                    context,
                    "NEET 2027: ${state.days}d ${state.hours}h ${state.minutes}m ${state.seconds}s",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            state: CountdownState = CountdownState.calculate(isIstMode = true)
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_neet_countdown)

            // Populate Days : Hours : Minutes : Seconds countdown
            views.setTextViewText(R.id.widget_tv_days, state.days.toString())
            views.setTextViewText(R.id.widget_tv_hours, String.format("%02d", state.hours))
            views.setTextViewText(R.id.widget_tv_mins, String.format("%02d", state.minutes))
            views.setTextViewText(R.id.widget_tv_secs, String.format("%02d", state.seconds))

            val timeStr = try {
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
            } catch (e: Exception) {
                ""
            }

            val statusText = when {
                state.isExamStarted -> "NEET 2027 In Progress / Concluded!"
                state.days < 7 -> "FINAL SPRINT! • Target: 720 • $timeStr"
                state.days < 30 -> "${state.days}d Left • NCERT & Mocks • $timeStr"
                else -> "${state.totalDays} Days Left • Target: 720 • Synced $timeStr"
            }
            views.setTextViewText(R.id.widget_tv_status, statusText)

            // Setup click to open MainActivity
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingLaunch = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingLaunch)

            // Setup click to refresh widget
            val refreshIntent = Intent(context, NeetCountdownWidgetProvider::class.java).apply {
                action = NeetConstants.ACTION_REFRESH_WIDGET
            }
            val pendingRefresh = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_refresh, pendingRefresh)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, NeetCountdownWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
