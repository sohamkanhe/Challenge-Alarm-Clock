package com.example.alarm_clock.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.alarm_clock.MainActivity

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(
        triggerAtMillis: Long,
        alarmId: Int = DEFAULT_ALARM_ID,
        challengeType: ChallengeType = ChallengeType.MathPuzzle
    ): ScheduleResult {
        if (!canScheduleExactAlarms()) {
            return ScheduleResult.ExactAlarmPermissionRequired
        }

        val operation = PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
                putExtra(EXTRA_CHALLENGE_TYPE, challengeType.name)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val showIntent = PendingIntent.getActivity(
            context,
            alarmId,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                operation
            )
            ScheduleResult.Scheduled
        } catch (exception: SecurityException) {
            ScheduleResult.ExactAlarmPermissionRequired
        } catch (exception: RuntimeException) {
            ScheduleResult.Failed(exception.message ?: "Unable to schedule alarm")
        }
    }

    fun cancel(alarmId: Int = DEFAULT_ALARM_ID) {
        val operation = PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        operation?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    companion object {
        const val DEFAULT_ALARM_ID = 1001
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_CHALLENGE_TYPE = "extra_challenge_type"

        fun exactAlarmSettingsIntent(context: Context): Intent =
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
    }
}

sealed interface ScheduleResult {
    data object Scheduled : ScheduleResult
    data object ExactAlarmPermissionRequired : ScheduleResult
    data class Failed(val message: String) : ScheduleResult
}

enum class ChallengeType(val title: String) {
    MathPuzzle("Math puzzle"),
    MemoryMatch("Memory match"),
    SequenceRecall("Sequence recall");

    companion object {
        fun fromName(name: String?): ChallengeType =
            entries.firstOrNull { it.name == name } ?: MathPuzzle
    }
}
