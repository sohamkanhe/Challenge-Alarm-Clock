package com.example.alarm_clock

import android.Manifest
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.alarm_clock.alarm.AlarmScheduler
import com.example.alarm_clock.alarm.ChallengeType
import com.example.alarm_clock.alarm.ScheduleResult
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlarmSetupScreen(
                        onSchedule = { triggerAtMillis, challengeType ->
                            AlarmScheduler(this).schedule(
                                triggerAtMillis = triggerAtMillis,
                                challengeType = challengeType
                            )
                        },
                        onOpenExactAlarmSettings = {
                            try {
                                startActivity(AlarmScheduler.exactAlarmSettingsIntent(this))
                            } catch (exception: ActivityNotFoundException) {
                                // Some older or customized devices do not expose this settings screen.
                            }
                        },
                        onCancel = {
                            AlarmScheduler(this).cancel()
                        }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun AlarmSetupScreen(
    onSchedule: (Long, ChallengeType) -> ScheduleResult,
    onOpenExactAlarmSettings: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val initialTime = rememberInitialAlarmTime()
    var selectedHour by rememberSaveable { mutableStateOf(initialTime.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by rememberSaveable { mutableStateOf(initialTime.get(Calendar.MINUTE)) }
    var selectedChallenge by rememberSaveable { mutableStateOf(ChallengeType.MathPuzzle) }
    var scheduledText by rememberSaveable { mutableStateOf("No alarm scheduled") }
    val selectedTimeText = formatSelectedTime(selectedHour, selectedMinute)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Challenge Alarm",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = scheduledText,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        ChallengePicker(
            selectedChallenge = selectedChallenge,
            onChallengeSelected = { selectedChallenge = it }
        )
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedHour = hourOfDay
                        selectedMinute = minute
                    },
                    selectedHour,
                    selectedMinute,
                    false
                ).show()
            }
        ) {
            Text("Time: $selectedTimeText")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val triggerAtMillis = nextTriggerAtMillis(selectedHour, selectedMinute)
                    when (val result = onSchedule(triggerAtMillis, selectedChallenge)) {
                        ScheduleResult.Scheduled -> {
                            scheduledText = "Scheduled for ${formatScheduledTime(triggerAtMillis)}"
                        }

                        ScheduleResult.ExactAlarmPermissionRequired -> {
                            scheduledText = "Allow Alarms & reminders, then set the alarm again"
                            onOpenExactAlarmSettings()
                        }

                        is ScheduleResult.Failed -> {
                            scheduledText = result.message
                        }
                    }
                }
            ) {
                Text("Set alarm")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onCancel()
                    scheduledText = "No alarm scheduled"
                }
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun rememberInitialAlarmTime(): Calendar =
    androidx.compose.runtime.remember {
        Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }
    }

@Composable
private fun ChallengePicker(
    selectedChallenge: ChallengeType,
    onChallengeSelected: (ChallengeType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ChallengeType.entries.forEach { challengeType ->
            FilterChip(
                selected = challengeType == selectedChallenge,
                onClick = { onChallengeSelected(challengeType) },
                label = { Text(challengeType.title) }
            )
        }
    }
}

private fun nextTriggerAtMillis(hour: Int, minute: Int): Long {
    val now = Calendar.getInstance()
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (!after(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }.timeInMillis
}

private fun formatSelectedTime(hour: Int, minute: Int): String =
    DateFormat.getTimeInstance(DateFormat.SHORT).format(
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    )

private fun formatScheduledTime(triggerAtMillis: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(
        Date(triggerAtMillis)
    )
