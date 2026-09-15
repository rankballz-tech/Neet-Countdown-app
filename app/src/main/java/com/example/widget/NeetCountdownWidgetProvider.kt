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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == NeetConstants.ACTION_REFRESH_WIDGET ||
            intent.action == NeetConstants.ACTION_UPDATE_WIDGET
        ) {
            updateAllWidgets(context)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val state = CountdownState.calculate(isIstMode = true)

            val views = RemoteViews(context.packageName, R.layout.widget_neet_countdown)

            // Populate countdown text values
            views.setTextViewText(R.id.widget_tv_days, state.days.toString())
            views.setTextViewText(R.id.widget_tv_hours, String.format("%02d", state.hours))
            views.setTextViewText(R.id.widget_tv_mins, String.format("%02d", state.minutes))
            views.setTextViewText(R.id.widget_tv_secs, String.format("%02d", state.seconds))

            val statusText = when {
                state.isExamStarted -> "NEET 2027 In Progress / Concluded!"
                state.days < 7 -> "FINAL SPRINT! Revision mode on."
                state.days < 30 -> "${state.days} days left! Daily mock tests & NCERT review."
                state.days < 100 -> "100-Day Zone: Speed, Accuracy & Biology mastery."
                else -> "${state.totalDays} Days Left • Target: 720/720 • Dream GMC"
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
