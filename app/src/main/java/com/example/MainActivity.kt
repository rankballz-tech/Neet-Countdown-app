package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notification.NeetNotificationHelper
import com.example.ui.NeetCountdownViewModel
import com.example.ui.components.CountdownHeroSection
import com.example.ui.components.MilestoneTimelineCard
import com.example.ui.components.NotificationControlCard
import com.example.ui.components.WidgetPinCard
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryCyan

class MainActivity : ComponentActivity() {

    private val viewModel: NeetCountdownViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                NeetCountdownApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeetCountdownApp(
    viewModel: NeetCountdownViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var hasNotificationPermission by remember {
        mutableStateOf(NeetNotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            viewModel.setSnackbar("Notification permission granted! Deadline alerts are active.")
            viewModel.sendTestNotification()
        } else {
            viewModel.setSnackbar("Notification permission was denied. You may miss deadline alerts.")
        }
    }

    // React to snackbar updates
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.setSnackbar(null)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("neet_countdown_screen"),
        containerColor = DarkNavyBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = "Medical Cross",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "NEET 2027 Countdown",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(AccentEmerald)
                                )
                            }
                            Text(
                                text = "May 2, 2027 • 2:00 PM IST",
                                fontSize = 11.sp,
                                color = PrimaryCyan
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.sendTestNotification()
                            }
                        },
                        modifier = Modifier.testTag("topbar_btn_test_notification")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Send Test Alert",
                            tint = PrimaryCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkNavySurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Countdown Unit
            item {
                CountdownHeroSection(
                    state = uiState.countdown,
                    isIstMode = uiState.isIstMode,
                    onToggleTimezone = { viewModel.toggleTimezoneMode() }
                )
            }

            // 2. Home Screen AppWidget Setup
            item {
                WidgetPinCard(
                    state = uiState.countdown,
                    onPinWidget = { viewModel.pinAppWidgetToHomeScreen(context) }
                )
            }

            // 3. Push Alerts & Milestone Notification Controls
            item {
                NotificationControlCard(
                    hasPermission = hasNotificationPermission,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onSendTestNotification = {
                        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.sendTestNotification()
                        }
                    },
                    dailyReminderEnabled = uiState.dailyReminderEnabled,
                    dailyReminderHour = uiState.dailyReminderHour,
                    dailyReminderMinute = uiState.dailyReminderMinute,
                    onToggleDailyReminder = { viewModel.toggleDailyReminder(it) },
                    onSelectTime = { hour, minute -> viewModel.updateDailyReminderTime(hour, minute) }
                )
            }

            // 4. NEET 2027 Countdown Milestone Timeline
            item {
                MilestoneTimelineCard(milestones = uiState.milestones)
            }
        }
    }
}

