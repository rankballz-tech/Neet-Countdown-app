package com.example.ui

import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.model.CountdownState
import com.example.model.ExamMilestone
import com.example.model.NeetConstants
import com.example.notification.NeetNotificationHelper
import com.example.widget.NeetCountdownWidgetProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class NeetUiState(
    val countdown: CountdownState = CountdownState.calculate(isIstMode = true),
    val isIstMode: Boolean = true,
    val milestones: List<ExamMilestone> = emptyList(),
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderHour: Int = 8,
    val dailyReminderMinute: Int = 0,
    val snackbarMessage: String? = null
)

class NeetCountdownViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(NeetConstants.PREFS_NAME, Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        NeetUiState(
            isIstMode = prefs.getBoolean(NeetConstants.KEY_TIMEZONE_IST, true),
            dailyReminderEnabled = prefs.getBoolean(NeetConstants.KEY_DAILY_REMINDER_ENABLED, true),
            dailyReminderHour = prefs.getInt(NeetConstants.KEY_DAILY_REMINDER_HOUR, 8),
            dailyReminderMinute = prefs.getInt(NeetConstants.KEY_DAILY_REMINDER_MINUTE, 0),
            milestones = NeetNotificationHelper.getUpcomingMilestones()
        )
    )
    val uiState: StateFlow<NeetUiState> = _uiState.asStateFlow()

    init {
        val context = application.applicationContext
        NeetNotificationHelper.createNotificationChannel(context)
        NeetNotificationHelper.scheduleAllMilestones(context)
        if (_uiState.value.dailyReminderEnabled) {
            NeetNotificationHelper.scheduleDailyReminder(
                context,
                _uiState.value.dailyReminderHour,
                _uiState.value.dailyReminderMinute
            )
        }

        // Periodic Countdown Ticking
        viewModelScope.launch {
            while (isActive) {
                val isIst = _uiState.value.isIstMode
                val updatedState = CountdownState.calculate(isIstMode = isIst)
                _uiState.update { it.copy(countdown = updatedState) }
                delay(1000)
            }
        }
    }

    fun toggleTimezoneMode() {
        val newMode = !_uiState.value.isIstMode
        prefs.edit().putBoolean(NeetConstants.KEY_TIMEZONE_IST, newMode).apply()
        _uiState.update {
            it.copy(
                isIstMode = newMode,
                countdown = CountdownState.calculate(isIstMode = newMode)
            )
        }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        val context = getApplication<Application>().applicationContext
        prefs.edit().putBoolean(NeetConstants.KEY_DAILY_REMINDER_ENABLED, enabled).apply()
        _uiState.update { it.copy(dailyReminderEnabled = enabled) }

        if (enabled) {
            NeetNotificationHelper.scheduleDailyReminder(
                context,
                _uiState.value.dailyReminderHour,
                _uiState.value.dailyReminderMinute
            )
            setSnackbar("Daily countdown reminder scheduled for %02d:%02d".format(_uiState.value.dailyReminderHour, _uiState.value.dailyReminderMinute))
        } else {
            setSnackbar("Daily reminder disabled.")
        }
    }

    fun updateDailyReminderTime(hour: Int, minute: Int) {
        val context = getApplication<Application>().applicationContext
        prefs.edit()
            .putInt(NeetConstants.KEY_DAILY_REMINDER_HOUR, hour)
            .putInt(NeetConstants.KEY_DAILY_REMINDER_MINUTE, minute)
            .apply()

        _uiState.update {
            it.copy(dailyReminderHour = hour, dailyReminderMinute = minute)
        }

        if (_uiState.value.dailyReminderEnabled) {
            NeetNotificationHelper.scheduleDailyReminder(context, hour, minute)
            setSnackbar("Daily reminder time updated to %02d:%02d".format(hour, minute))
        }
    }

    fun sendTestNotification() {
        val context = getApplication<Application>().applicationContext
        NeetNotificationHelper.sendImmediateTestNotification(context)
        NeetCountdownWidgetProvider.updateAllWidgets(context)
        setSnackbar("Alert sent! Check your notification tray.")
    }

    fun pinAppWidgetToHomeScreen(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, NeetCountdownWidgetProvider::class.java)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successIntent = Intent(context, MainActivity::class.java)
                val successPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    successIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(provider, null, successPendingIntent)
                setSnackbar("Widget pin prompt displayed on home screen!")
                return true
            }
        }
        setSnackbar("Touch and hold on home screen to add 'NEET 2027 Countdown' widget.")
        return false
    }

    fun setSnackbar(msg: String?) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }
}
