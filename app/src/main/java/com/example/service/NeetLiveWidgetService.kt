package com.example.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import com.example.model.CountdownState
import com.example.model.NeetConstants
import com.example.notification.NeetNotificationHelper
import com.example.widget.NeetCountdownWidgetProvider

class NeetLiveWidgetService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var isTicking = false
    private var screenReceiverRegistered = false

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (!isTicking) return

            val state = CountdownState.calculate(isIstMode = true)

            // Update all active home screen widgets
            val appWidgetManager = AppWidgetManager.getInstance(this@NeetLiveWidgetService)
            val thisWidget = ComponentName(this@NeetLiveWidgetService, NeetCountdownWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            if (widgetIds.isNotEmpty()) {
                for (id in widgetIds) {
                    NeetCountdownWidgetProvider.updateAppWidget(
                        this@NeetLiveWidgetService,
                        appWidgetManager,
                        id,
                        state
                    )
                }
            }

            // Sync ongoing notification
            if (NeetNotificationHelper.isOngoingNotificationEnabled(this@NeetLiveWidgetService)) {
                NeetNotificationHelper.showOngoingCountdownNotification(this@NeetLiveWidgetService, state)
            }

            // If no widgets on home screen and ongoing notification disabled, stop service
            if (widgetIds.isEmpty() && !NeetNotificationHelper.isOngoingNotificationEnabled(this@NeetLiveWidgetService)) {
                stopSelf()
                return
            }

            // Schedule next tick aligned with system second edge to prevent drift
            val currentMillis = System.currentTimeMillis()
            val delayMillis = (1000L - (currentMillis % 1000L)).coerceIn(200L, 1000L)
            handler.postDelayed(this, delayMillis)
        }
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> startTicking()
                Intent.ACTION_SCREEN_OFF -> stopTicking()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(screenReceiver, filter)
        }
        screenReceiverRegistered = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val state = CountdownState.calculate(isIstMode = true)
        val notification = NeetNotificationHelper.buildOngoingNotification(this, state)
        try {
            startForeground(NeetConstants.ONGOING_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            // Android 14+ exception safety
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isScreenOn = powerManager?.isInteractive ?: true
        if (isScreenOn) {
            startTicking()
        } else {
            stopTicking()
        }

        return START_STICKY
    }

    private fun startTicking() {
        if (!isTicking) {
            isTicking = true
            handler.removeCallbacks(tickRunnable)
            handler.post(tickRunnable)
        }
    }

    private fun stopTicking() {
        isTicking = false
        handler.removeCallbacks(tickRunnable)
    }

    override fun onDestroy() {
        stopTicking()
        if (screenReceiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
            } catch (e: Exception) {
                // Receiver may already be unregistered
            }
            screenReceiverRegistered = false
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, NeetLiveWidgetService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                // If background start is restricted, do static update
                NeetCountdownWidgetProvider.updateAllWidgets(context)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, NeetLiveWidgetService::class.java)
            context.stopService(intent)
        }
    }
}
