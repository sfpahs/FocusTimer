package com.example.focustimer

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.navigation.NavHostController
import com.example.focustimer.watchModel.WatchData
import com.example.focustimer.watchModel.WatchViewModel
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceWatchCommunication : Service(), MessageClient.OnMessageReceivedListener{
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val dataClient by lazy { Wearable.getDataClient(this) }

    private val navController: NavHostController? = null

    private val viewModel by lazy { WatchViewModel.getInstance() }

    inner class LocalBinder : Binder() {
        fun getService(): ServiceWatchCommunication = this@ServiceWatchCommunication
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }



    override fun onCreate() {
        super.onCreate()
        messageClient.addListener(this)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/timer_action" -> {
                var action = String(messageEvent.data)
                when (action) {
                    "switch" -> {
                        val intent = Intent(this, TimerService::class.java).apply {
                            action = TimerService.ACTION_SWITCH
                        }
                        startService(intent)
                    }
                    "stop" -> {
                        val intent = Intent(this, TimerService::class.java).apply {
                            action = TimerService.ACTION_STOP
                        }
                        startService(intent)
                    }
                }
            }
            "/navigation" -> {
                val destination = String(messageEvent.data)
                val broadcastIntent = Intent("com.example.pre_capstone.NAVIGATION_ACTION")
                broadcastIntent.putExtra("destination", destination)
                sendBroadcast(broadcastIntent)
            }
        }
    }


    fun sendTimerStatusToWatch() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val time = viewModel.time.value
                val active = viewModel.activeTimer.value
                val setting = viewModel.setting.value

                val watchData = WatchData(timerSetting = setting, activeTimer = active, time = time)
                val gson = Gson()
                val jsonWatchData = gson.toJson(watchData)
                val request = PutDataMapRequest.create("/timer_status").apply {
                    dataMap.putString("watch_data_json",jsonWatchData)
//                    dataMap.putString("timer_name", setting.name)
//                    dataMap.putInt("active_stopwatch", active)
//                    dataMap.putInt("time",time)
                }

                val putDataReq = request.asPutDataRequest()
                dataClient.putDataItem(putDataReq)
            } catch (e: Exception) {
                Log.e("WatchComm", "워치에 데이터 전송 실패: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        messageClient.removeListener(this)

        super.onDestroy()
    }
}
