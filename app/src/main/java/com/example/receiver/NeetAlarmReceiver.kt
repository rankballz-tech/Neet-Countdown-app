package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.model.CountdownState
import com.example.model.NeetConstants
import com.example.notification.NeetNotificationHelper
import com.example.widget.NeetCountdownWidgetProvider

class NeetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NeetConstants.ACTION_MILESTONE_ALERT -> {
                val milestoneId = intent.getIntExtra("milestone_id", 3001)
                val title = intent.getStringExtra("milestone_title") ?: "NEET 2027 Milestone Alert"
                val desc = intent.getStringExtra("milestone_desc") ?: "Major milestone reached toward NEET 2027!"
                val state = CountdownState.calculate(isIstMode = true)
                val fullBody = "$desc\n(Remaining: ${state.days} days, ${state.hours} hours)"

                NeetNotificationHelper.showCountdownAlert(
                    context = context,
                    title = "🎯 $title",
                    body = fullBody,
                    notificationId = milestoneId
                )
                NeetCountdownWidgetProvider.updateAllWidgets(context)
            }

            NeetConstants.ACTION_DAILY_REMINDER -> {
                val state = CountdownState.calculate(isIstMode = true)
                val dailyTips = listOf(
                    "Master NCERT line-by-line in Biology for 360/360 marks.",
                    "Practice 45 Physics MCQs with a strict 45-minute timer today.",
                    "Revise Organic Chemistry name reactions and mechanisms.",
                    "Review your error notebook before solving today's mock test.",
                    "Focus on Genetics, Biotechnology, and Human Physiology high-weightage chapters.",
                    "Speed + Accuracy = GMC seat. Eliminate negative marking!",
                    "Consistency is key. Every chapter you master brings you closer to your stethoscope."
                )
                val randomTip = dailyTips.random()

                NeetNotificationHelper.showCountdownAlert(
                    context = context,
                    title = "🩺 NEET 2027: ${state.days} Days & ${state.hours}h Left!",
                    body = "Daily Motivation: $randomTip",
                    notificationId = 4001
                )
                NeetCountdownWidgetProvider.updateAllWidgets(context)
            }

            NeetConstants.ACTION_UPDATE_WIDGET -> {
                NeetCountdownWidgetProvider.updateAllWidgets(context)
                com.example.service.NeetLiveWidgetService.start(context)
                if (NeetNotificationHelper.isOngoingNotificationEnabled(context)) {
                    NeetNotificationHelper.showOngoingCountdownNotification(context)
                }
                // Schedule the next periodic update to maintain continuous background updates
                NeetNotificationHelper.scheduleWidgetPeriodicUpdates(context)
            }

            NeetConstants.ACTION_REFRESH_WIDGET -> {
                NeetCountdownWidgetProvider.updateAllWidgets(context)
                com.example.service.NeetLiveWidgetService.start(context)
                if (NeetNotificationHelper.isOngoingNotificationEnabled(context)) {
                    NeetNotificationHelper.showOngoingCountdownNotification(context)
                }
            }
        }
    }
}
