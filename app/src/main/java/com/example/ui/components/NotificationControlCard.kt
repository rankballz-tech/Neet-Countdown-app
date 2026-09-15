package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.PrimaryCyan

@Composable
fun NotificationControlCard(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onSendTestNotification: () -> Unit,
    dailyReminderEnabled: Boolean,
    dailyReminderHour: Int,
    dailyReminderMinute: Int,
    onToggleDailyReminder: (Boolean) -> Unit,
    onSelectTime: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(1.dp, DarkNavyBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AccentEmerald.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alerts",
                            tint = AccentEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Push Notifications & Alerts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Countdown milestones & daily study reminders",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permission Warning Banner if missing
            if (!hasPermission) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentAmber.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Warning",
                            tint = AccentAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notification Permission Required",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFEF3C7)
                            )
                            Text(
                                text = "Grant permission to receive deadline alerts.",
                                fontSize = 11.sp,
                                color = Color(0xFFFDE68A)
                            )
                        }
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Grant", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Instant Push Alert Test Button
            OutlinedButton(
                onClick = onSendTestNotification,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_send_test_notification"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryCyan
                ),
                border = BorderStroke(1.dp, PrimaryCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Immediate Test Push Alert",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Reminder Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111827), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Daily Reminder",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Countdown Reminder",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Alerts remaining days + daily NCERT tip",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Switch(
                    checked = dailyReminderEnabled,
                    onCheckedChange = onToggleDailyReminder,
                    modifier = Modifier.testTag("switch_daily_reminder"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentEmerald,
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = DarkNavyBorder
                    )
                )
            }

            if (dailyReminderEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Scheduled Alert Time:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val times = listOf(
                        Triple(6, 0, "06:00 AM"),
                        Triple(8, 0, "08:00 AM"),
                        Triple(14, 0, "02:00 PM"),
                        Triple(21, 0, "09:00 PM")
                    )

                    for ((hour, minute, label) in times) {
                        val isSelected = dailyReminderHour == hour && dailyReminderMinute == minute
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectTime(hour, minute) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = Color(0xFF00354E),
                                containerColor = Color(0xFF111827),
                                labelColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Milestones Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AccentEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Automatic AlarmManager alerts armed for 1Y, 6M, 100D, 30D, 7D, 24H & 1H.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
