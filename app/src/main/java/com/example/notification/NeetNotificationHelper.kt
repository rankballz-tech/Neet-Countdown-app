package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.CountdownState
import com.example.model.ExamMilestone
import com.example.model.NeetConstants
import com.example.receiver.NeetAlarmReceiver
import java.time.ZonedDateTime
import java.util.Calendar

object NeetNotificationHelper {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NeetConstants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showCountdownAlert(
        context: Context,
        title: String,
        body: String,
        notificationId: Int
    ) {
        createNotificationChannel(context)

        if (!hasNotificationPermission(context)) {
            return
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NeetConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medical_cross)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun sendImmediateTestNotification(context: Context) {
        val state = CountdownState.calculate(isIstMode = true)
        val title = "⚡ NEET 2027 Alert: Test Notification"
        val body = "Countdown: ${state.days}d ${state.hours}h ${state.minutes}m ${state.seconds}s until May 2, 2027 at 2:00 PM IST. Stay consistent with your revision!"
        showCountdownAlert(context, title, body, notificationId = 1001)
    }

    fun getUpcomingMilestones(): List<ExamMilestone> {
        val target = NeetConstants.TARGET_ZONED_DATETIME

        return listOf(
            ExamMilestone(
                id = 201,
                title = "1 Year Countdown Mark",
                description = "Exactly 365 days until NEET 2027. Finish Class 11 and 12 core syllabi.",
                epochMillis = target.minusYears(1).toInstant().toEpochMilli(),
                formattedDate = "May 2, 2026 • 2:00 PM"
            ),
            ExamMilestone(
                id = 202,
                title = "6 Months Sprint",
                description = "Half-year mark. Transition to intensive NCERT line-by-line reading and topic tests.",
                epochMillis = target.minusMonths(6).toInstant().toEpochMilli(),
                formattedDate = "Nov 2, 2026 • 2:00 PM"
            ),
            ExamMilestone(
                id = 203,
                title = "100 Days Countdown",
                description = "Critical 100-day boundary! Commence daily full syllabus mock tests (2:00 PM - 5:20 PM).",
                epochMillis = target.minusDays(100).toInstant().toEpochMilli(),
                formattedDate = "Jan 22, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 204,
                title = "60 Days to NEET",
                description = "Focus on weak chapters, error book analysis, and high-frequency numericals.",
                epochMillis = target.minusDays(60).toInstant().toEpochMilli(),
                formattedDate = "March 3, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 205,
                title = "30 Days Final Lap",
                description = "One month to go! Complete 20 full-length previous year papers with OMR practice.",
                epochMillis = target.minusDays(30).toInstant().toEpochMilli(),
                formattedDate = "April 2, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 206,
                title = "15 Days Alert",
                description = "Two weeks left. Revise formula sheets, plant/animal kingdom charts, and organic pathways.",
                epochMillis = target.minusDays(15).toInstant().toEpochMilli(),
                formattedDate = "April 17, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 207,
                title = "7 Days (Final Week)",
                description = "One week remaining. Maintain your biological sleep cycle for 2:00 PM - 5:20 PM peak alertness.",
                epochMillis = target.minusDays(7).toInstant().toEpochMilli(),
                formattedDate = "April 25, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 208,
                title = "3 Days to Go",
                description = "Review admit card guidelines, exam center logistics, and high-yield flashcards.",
                epochMillis = target.minusDays(3).toInstant().toEpochMilli(),
                formattedDate = "April 29, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 209,
                title = "24 Hours (Eve of Exam)",
                description = "Tomorrow at 2:00 PM is your moment. Rest your mind, eat healthy, and stay confident!",
                epochMillis = target.minusHours(24).toInstant().toEpochMilli(),
                formattedDate = "May 1, 2027 • 2:00 PM"
            ),
            ExamMilestone(
                id = 210,
                title = "3 Hours to Exam Gate Closure",
                description = "Exam Day! Head to exam center, carry your admit card, passport photos, and valid ID.",
                epochMillis = target.minusHours(3).toInstant().toEpochMilli(),
                formattedDate = "May 2, 2027 • 11:00 AM"
            ),
            ExamMilestone(
                id = 211,
                title = "1 Hour to NEET 2027 Exam",
                description = "Entry hall seated. Deep breaths, trust your preparation. Best of luck Doctor!",
                epochMillis = target.minusHours(1).toInstant().toEpochMilli(),
                formattedDate = "May 2, 2027 • 1:00 PM"
            )
        )
    }

    fun scheduleAllMilestones(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = System.currentTimeMillis()

        val milestones = getUpcomingMilestones()
        for (m in milestones) {
            if (m.epochMillis > now) {
                val intent = Intent(context, NeetAlarmReceiver::class.java).apply {
                    action = NeetConstants.ACTION_MILESTONE_ALERT
                    putExtra("milestone_id", m.id)
                    putExtra("milestone_title", m.title)
                    putExtra("milestone_desc", m.description)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    m.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            m.epochMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            m.epochMillis,
                            pendingIntent
                        )
                    }
                } catch (e: SecurityException) {
                    // Inexact fallback if exact alarm permission not granted
                    alarmManager.set(AlarmManager.RTC_WAKEUP, m.epochMillis, pendingIntent)
                }
            }
        }
    }

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, NeetAlarmReceiver::class.java).apply {
            action = NeetConstants.ACTION_DAILY_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            5001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
