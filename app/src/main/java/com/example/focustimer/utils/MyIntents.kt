package com.example.focustimer.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.focustimer.Activity.MainActivity
import com.example.focustimer.service.TimerService
import com.example.focustimer.service.TimerService.Companion.ACTION_STOP
import com.example.focustimer.service.TimerService.Companion.ACTION_SWITCH

object MyIntents {
    fun getSwitchWatchIntent(context : Context): PendingIntent? {
        val switchIntent = Intent(context, TimerService::class.java).apply {
            action = ACTION_SWITCH
        }

        val switchPendingIntent = PendingIntent.getService(
            context, 1, switchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return switchPendingIntent
    }

    fun getStopWatchIntent(context: Context) : PendingIntent?{
        val stopIntent = Intent(context, TimerService::class.java).apply {
            action = ACTION_STOP
        }

        return PendingIntent.getService(
            context,
            2,
             stopIntent, // 액티비티 실행 후 서비스 종료
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun getNotificationIntent(context: Context): PendingIntent?{
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }

}