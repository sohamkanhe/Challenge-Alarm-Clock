package com.example.alarm_clock.challenge

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alarm_clock.alarm.AlarmScheduler
import com.example.alarm_clock.alarm.ChallengeType
import com.example.alarm_clock.alarm.RingtonePlayer

class ChallengeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareLockScreen()

        val challengeType = ChallengeType.fromName(
            intent.getStringExtra(AlarmScheduler.EXTRA_CHALLENGE_TYPE)
        )
        val alarmId = intent.getIntExtra(
            AlarmScheduler.EXTRA_ALARM_ID,
            AlarmScheduler.DEFAULT_ALARM_ID
        )

        RingtonePlayer.start(this)
        setContent {
            ChallengeAlarmTheme {
                ChallengeAlarmScreen(
                    challengeType = challengeType,
                    onSolved = {
                        RingtonePlayer.stop()
                        getSystemService(NotificationManager::class.java).cancel(alarmId)
                        finishAndRemoveTask()
                    }
                )
            }
        }
    }

    private fun prepareLockScreen() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        getSystemService(KeyguardManager::class.java).requestDismissKeyguard(this, null)
    }

    companion object {
        fun createIntent(
            context: Context,
            challengeType: ChallengeType,
            alarmId: Int
        ): Intent = Intent(context, ChallengeActivity::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_CHALLENGE_TYPE, challengeType.name)
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
        }
    }
}

@Composable
private fun ChallengeAlarmTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun ChallengeAlarmScreen(
    challengeType: ChallengeType,
    onSolved: () -> Unit
) {
    var solved by rememberSaveable(challengeType) { mutableStateOf(false) }
    var stopHandled by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {}
    LaunchedEffect(solved) {
        if (solved && !stopHandled) {
            stopHandled = true
            onSolved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (challengeType) {
            ChallengeType.MathPuzzle -> MathPuzzleChallenge(onSolvedChange = { solved = it })
            ChallengeType.MemoryMatch -> MemoryMatchChallenge(onSolvedChange = { solved = it })
            ChallengeType.SequenceRecall -> SequenceRecallChallenge(onSolvedChange = { solved = it })
        }
    }
}
