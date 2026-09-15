package com.example.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object NeetConstants {
    val IST_ZONE: ZoneId = ZoneId.of("Asia/Kolkata")

    // NEET 2027 Exam Date: 2 May 2027, 2:00:00 PM IST
    val TARGET_ZONED_DATETIME: ZonedDateTime = ZonedDateTime.of(
        2027, 5, 2, 14, 0, 0, 0, IST_ZONE
    )

    val TARGET_EPOCH_MILLIS: Long = TARGET_ZONED_DATETIME.toInstant().toEpochMilli()

    // Typical preparation baseline: May 2, 2025 (2 years prep window)
    val BASELINE_EPOCH_MILLIS: Long = ZonedDateTime.of(
        2025, 5, 2, 14, 0, 0, 0, IST_ZONE
    ).toInstant().toEpochMilli()

    const val ACTION_REFRESH_WIDGET = "com.example.ACTION_REFRESH_WIDGET"
    const val ACTION_MILESTONE_ALERT = "com.example.ACTION_MILESTONE_ALERT"
    const val ACTION_DAILY_REMINDER = "com.example.ACTION_DAILY_REMINDER"
    const val ACTION_UPDATE_WIDGET = "com.example.ACTION_UPDATE_WIDGET"

    const val CHANNEL_ID = "neet_2027_countdown_alerts"
    const val PREFS_NAME = "neet_countdown_prefs"
    const val KEY_DAILY_REMINDER_ENABLED = "daily_reminder_enabled"
    const val KEY_DAILY_REMINDER_HOUR = "daily_reminder_hour"
    const val KEY_DAILY_REMINDER_MINUTE = "daily_reminder_minute"
    const val KEY_TIMEZONE_IST = "timezone_ist_mode"
}

data class CountdownState(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val totalDays: Long,
    val totalHours: Long,
    val totalMinutes: Long,
    val totalSeconds: Long,
    val progressFraction: Float,
    val isExamStarted: Boolean,
    val targetFormatted: String
) {
    companion object {
        fun calculate(isIstMode: Boolean = true): CountdownState {
            val now = Instant.now()
            val targetInstant = Instant.ofEpochMilli(NeetConstants.TARGET_EPOCH_MILLIS)

            val diffMillis = targetInstant.toEpochMilli() - now.toEpochMilli()

            if (diffMillis <= 0) {
                return CountdownState(
                    days = 0,
                    hours = 0,
                    minutes = 0,
                    seconds = 0,
                    totalDays = 0,
                    totalHours = 0,
                    totalMinutes = 0,
                    totalSeconds = 0,
                    progressFraction = 1f,
                    isExamStarted = true,
                    targetFormatted = "May 2, 2027 • 2:00 PM IST"
                )
            }

            val totalSeconds = diffMillis / 1000
            val totalMinutes = totalSeconds / 60
            val totalHours = totalMinutes / 60
            val totalDays = totalHours / 24

            val seconds = totalSeconds % 60
            val minutes = totalMinutes % 60
            val hours = totalHours % 24
            val days = totalDays

            // Calculate progress relative to baseline
            val totalSpan = (NeetConstants.TARGET_EPOCH_MILLIS - NeetConstants.BASELINE_EPOCH_MILLIS).toFloat()
            val elapsed = (now.toEpochMilli() - NeetConstants.BASELINE_EPOCH_MILLIS).toFloat()
            val progress = (elapsed / totalSpan).coerceIn(0f, 1f)

            val displayTz = if (isIstMode) "IST" else ZoneId.systemDefault().id
            val targetFormatted = "Sunday, May 2, 2027 • 2:00 PM $displayTz"

            return CountdownState(
                days = days,
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                totalDays = totalDays,
                totalHours = totalHours,
                totalMinutes = totalMinutes,
                totalSeconds = totalSeconds,
                progressFraction = progress,
                isExamStarted = false,
                targetFormatted = targetFormatted
            )
        }
    }
}

data class ExamMilestone(
    val id: Int,
    val title: String,
    val description: String,
    val epochMillis: Long,
    val formattedDate: String
) {
    val isPassed: Boolean
        get() = System.currentTimeMillis() >= epochMillis
}

data class HighYieldTopic(
    val subject: String,
    val chapter: String,
    val weightage: String,
    val keyTips: String
)
