package com.example.focustimer.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.focustimer.Activity.MainActivity
import com.example.focustimer.TimerService
import com.example.focustimer.TimerService.Companion.ACTION_STOP
import com.example.focustimer.TimerService.Companion.ACTION_SWITCH

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
        // [1] 메인 액티비티 실행 인텐트
//        val mainIntent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or // 기존 인스턴스 재활용
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//            putExtra("from_notification_stop", true) // 식별용 추가 데이터
//        }
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