package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CountdownState
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryTeal

@Composable
fun CountdownHeroSection(
    state: CountdownState,
    isIstMode: Boolean,
    onToggleTimezone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_live")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("countdown_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(PrimaryCyan.copy(alpha = 0.6f), SecondaryTeal.copy(alpha = 0.3f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryCyan.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryTeal)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentEmerald)
                                .alpha(pulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE COUNTDOWN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                // Timezone Chip button
                Surface(
                    onClick = onToggleTimezone,
                    shape = RoundedCornerShape(12.dp),
                    color = DarkNavyBorder.copy(alpha = 0.5f),
                    modifier = Modifier.testTag("btn_toggle_timezone")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Timezone",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isIstMode) "IST (UTC+5:30)" else "Local Time",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFF1F5F9)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exam Title & Target details
            Text(
                text = "NEET UG 2027",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Target Date: 2 May 2027 • 2:00 PM",
                fontSize = 13.sp,
                color = PrimaryCyan,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "National Eligibility cum Entrance Test (UG)",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main 4 Countdown Units
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeUnitBox(
                    value = state.days,
                    label = "DAYS",
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
                TimeUnitBox(
                    value = state.hours,
                    label = "HOURS",
                    highlight = false,
                    modifier = Modifier.weight(1f)
                )
                TimeUnitBox(
                    value = state.minutes,
                    label = "MINS",
                    highlight = false,
                    modifier = Modifier.weight(1f)
                )
                TimeUnitBox(
                    value = state.seconds,
                    label = "SECS",
                    highlight = false,
                    isSeconds = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preparation Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Preparation Timeline Progress",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${(state.progressFraction * 100).toInt()}% Elapsed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { state.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryCyan,
                    trackColor = DarkNavyBorder
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111827), RoundedCornerShape(14.dp))
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubStatItem(
                    title = "Weeks Left",
                    value = "${state.totalDays / 7}",
                    icon = Icons.Default.CalendarMonth
                )
                SubStatItem(
                    title = "Total Hours",
                    value = "${state.totalHours}h",
                    icon = Icons.Default.AccessTime
                )
                SubStatItem(
                    title = "Remaining Days",
                    value = "${state.totalDays}d",
                    icon = Icons.Default.HourglassTop
                )
            }
        }
    }
}

@Composable
fun TimeUnitBox(
    value: Long,
    label: String,
    highlight: Boolean,
    isSeconds: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF111827),
        border = BorderStroke(
            width = if (highlight) 1.5.dp else 1.dp,
            color = if (highlight) PrimaryCyan.copy(alpha = 0.5f) else DarkNavyBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    if (isSeconds) {
                        // High-speed, jitter-free fade for seconds to prevent layout stutter
                        fadeIn(animationSpec = tween(120)) togetherWith
                                fadeOut(animationSpec = tween(120))
                    } else {
                        // Refined gentle vertical transition for days, hours, and minutes
                        slideInVertically(animationSpec = tween(220)) { height -> height / 2 } +
                                fadeIn(animationSpec = tween(220)) togetherWith
                                slideOutVertically(animationSpec = tween(220)) { height -> -height / 2 } +
                                fadeOut(animationSpec = tween(220))
                    }
                },
                label = "time_unit_$label"
            ) { targetValue ->
                Text(
                    text = String.format("%02d", targetValue),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    style = TextStyle(fontFeatureSettings = "tnum"),
                    color = when {
                        highlight -> PrimaryCyan
                        isSeconds -> AccentEmerald
                        else -> Color(0xFFF8FAFC)
                    },
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun SubStatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
