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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamMilestone
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.PrimaryCyan

@Composable
fun MilestoneTimelineCard(
    milestones: List<ExamMilestone>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(1.dp, DarkNavyBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryCyan.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Milestones",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "NEET 2027 Countdown Milestones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Scheduled push alerts approaching 2 May 2027",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (milestone in milestones) {
                    MilestoneItemRow(milestone = milestone)
                }
            }
        }
    }
}

@Composable
private fun MilestoneItemRow(
    milestone: ExamMilestone
) {
    val isPassed = milestone.isPassed

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isPassed) Color(0xFF111827).copy(alpha = 0.6f) else Color(0xFF111827),
        border = BorderStroke(
            1.dp,
            if (isPassed) DarkNavyBorder else PrimaryCyan.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPassed) AccentEmerald.copy(alpha = 0.2f)
                        else PrimaryCyan.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPassed) Icons.Default.Check else Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (isPassed) AccentEmerald else PrimaryCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = milestone.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPassed) Color(0xFF94A3B8) else Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isPassed) DarkNavyBorder else PrimaryCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isPassed) "COMPLETED" else "ARMED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPassed) Color(0xFF94A3B8) else PrimaryCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = milestone.formattedDate,
                    fontSize = 11.sp,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = milestone.description,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
