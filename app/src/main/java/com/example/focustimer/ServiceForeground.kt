package com.example.focustimer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.focustimer.utils.MyIntents
import com.example.shared.model.DateTimeWrapper
import com.example.shared.Myfirebase.saveHistoryData
import com.example.shared.model.HistoryData
import com.example.shared.model.TimerOptions
import com.example.shared.model.TimerViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class TimerService : Service() {
    companion object {
        const val CHANNEL_ID = "TimerServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.example.pre_capstone.START"
        const val ACTION_PAUSE = "com.example.pre_capstone.PAUSE"
        const val ACTION_STOP = "com.example.pre_capstone.TIMER_STOPPED"
        const val ACTION_SWITCH = "com.example.pre_capstone.SWITCH"
    }

    private val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    var setting = viewModel.currentSubject.value
    var time = viewModel.time.value
    var activeTimer = viewModel.activeTimer.value
    var option = viewModel.timerOption.value
    private lateinit var myService: ServiceWatchCommunication
    private var bound = false

    private val connection = object : ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ServiceWatchCommunication.LocalBinder
            myService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }
    // 콜백 인터페이스 정의
    interface TimerCallback {
        fun onTimerTick(timerWorkTimer: Int, restTimer: Int, activeStopwatch: Int)
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private val binder = TimerBinder()
    private var wakeLock: PowerManager.WakeLock? = null
    private var timerJob: Job? = null
    private var isRunning = false
    private var totalWorkTime = 0
    private var totalRestTime = 0
    private var workCount = 1
    private var startTime = LocalDateTime.MIN
    private var mulTime = false
    private var callback: TimerCallback? = null



    private fun checkAndRequestPermissions() {
        // 알림 권한 확인 (Android 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                val intent = Intent().apply {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("android.provider.extra.APP_PACKAGE", packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        }

        // 정확한 알람 권한 확인 (Android 12 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(this, android.app.AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                val alarmIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(alarmIntent)
            }
        }

        // 삼성 기기 배터리 최적화 설정

    }
    //todo삼성배터리 최적화 찾아볼것
    fun samsungBattery(){
        if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            startActivity(intent)
        }
    }
    fun checkBatteryOptimization() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            // 배터리 최적화가 활성화된 경우
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

        // 삼성 기기 추가 설정
        if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
            try {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.samsung.android.lool",
                        "com.samsung.android.sm.ui.battery.BatteryActivity"
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("Battery", "Samsung battery settings not available", e)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 서비스 시작 시 즉시 포그라운드로 전환
        Log.d("StartService", "onStartCommand: ${intent?.action}")
        startForeground(NOTIFICATION_ID, createNotification())

        intent?.action?.let {
            when (it) {
                ACTION_START -> startTimer()
                ACTION_PAUSE -> pauseTimer()
                ACTION_SWITCH -> switchTimer()
                ACTION_STOP -> stopTimer()
                // 기타 액션 처리...
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "타이머 알림 채널"
                enableLights(false)
                enableVibration(false)
                setSound(null, null) // 소리 비활성화
                setShowBadge(false) // 뱃지 비활성화
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun setCallback(callback: TimerCallback) {
        this.callback = callback
    }

    fun setMinuteMultiplier(enabled: Boolean) {
        this.mulTime = enabled
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            startTime = LocalDateTime.now()
            startTimerJob()
        }
    }

    private fun pauseTimer() {
        isRunning = false
        timerJob?.cancel()
        updateNotification()
    }

    private fun stopTimer() {
        isRunning = false
        timerJob?.cancel()

        if (activeTimer == 1) totalWorkTime += time
        else totalRestTime += time

        totalWorkTime /= 60
        totalRestTime /= 60

        val historyData = HistoryData(
            startTime = DateTimeWrapper(startTime),
            category = setting.id,
            totalMinute = totalWorkTime + totalRestTime,
            workingMinute = totalWorkTime,
            restMinute = totalRestTime,
            averageWorkingMinute = totalWorkTime / workCount
        )

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            saveHistoryData(historyData)
        }

        Log.d("TAG", "onReceive: 리시버던짐")
        // 브로드캐스트 전송 - UI에 타이머 종료 알림
        val broadcastIntent = Intent(ACTION_STOP).apply {
            putExtra("navigate_to_main", true)
            flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        }
        sendBroadcast(broadcastIntent)

        removeNotificationAndStopService()
    }

    private fun switchTimer() {
        timerJob?.cancel()

        viewModel.setActiveTimer(
            if (activeTimer == 1) {
            totalWorkTime += time
            viewModel.resetTimer()
            2
        }
            else {
            totalRestTime += time
            workCount++
            viewModel.resetTimer()
            1
        }
        )

        startTimerJob()
        updateNotification()
    }

    private fun startTimerJob() {
        acquireWakeLock()

        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(1000L)
                viewModel.increaseTimer()
                if (bound) {
                    myService.sendTimerStatusToWatch()
                }
                updateNotification()
            }
        }
    }



    private fun createNotification(): Notification {
        setting = viewModel.currentSubject.value
        time = viewModel.time.value
        activeTimer = viewModel.activeTimer.value
        option = viewModel.timerOption.value
        // 알림 바디 클릭 시 앱 실행 인텐트
        val pendingIntent = MyIntents.getNotificationIntent(this)
        // 스위치 버튼 인텐트
        val switchPendingIntent = MyIntents.getSwitchWatchIntent(this);
        // 종료 버튼 인텐트 - 앱을 실행하지 않고 서비스만 종료하는 특별 인텐트
        val stopPendingIntent = MyIntents.getStopWatchIntent(this)

        val timer = option
        val maxTime = if (activeTimer == 1) timer.workTime else timer.restTime
        val timerText = "${time / 60}:${(time % 60).toString().padStart(2, '0')} / ${maxTime / 60}:${(maxTime % 60).toString().padStart(2, '0')}"
        val activityType = if (activeTimer == 1) "작업 중" else "휴식 중"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${setting.name} - $activityType")
            .setContentText(timerText)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_switch, "체인지", switchPendingIntent)
            .addAction(R.drawable.ic_stop, "종료", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()
    }

    @SuppressLint("NotificationPermission")
    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun removeNotificationAndStopService() {
        // 알림 매니저를 통해 직접 알림 제거
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
        // 안드로이드 버전에 따라 다른 방식으로 포그라운드 서비스 중지
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }

        // 서비스 자체를 중지
        stopSelf()
        if (bound) {
            try {
                unbindService(connection)
                bound = false
            } catch (e: IllegalArgumentException) {
                Log.e("TimerService", "Service not bound", e)
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        removeNotificationAndStopService()
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        checkAndRequestPermissions()
        bindWatchCommunicationService()
    }

    override fun onDestroy() {
        super.onDestroy()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        timerJob?.cancel()
        unBindWatchCommunicationService()
       releaseWakeLock()
    }
    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "FocusTimer::TimerWakeLock"
        ).apply { acquire(10 * 60 * 1000L) }
    }

    // 안전한 null 처리
    private fun releaseWakeLock() {
        wakeLock?.takeIf { it.isHeld }?.release()
        wakeLock = null
    }
    private fun bindWatchCommunicationService() {
        if(!bound){

            val intent = Intent(this, ServiceWatchCommunication::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)

        }
    }

    private fun unBindWatchCommunicationService() {
        try {

            if (bound) {
                unbindService(connection)
                bound = false
            }
        }
        catch (e : IllegalArgumentException){

            Log.e("TimerService", "unBindWatchCommunicationService: ", e)

        }
    }





}
