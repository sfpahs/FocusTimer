package com.example.focustimer
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.focustimer.model.AppTimerViewModel
import com.example.focustimer.model.TimerSetting
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.example.focustimer.watchModel.WatchData
import com.example.focustimer.watchModel.WatchViewModel

class WatchDataLayerListenerService : WearableListenerService() {
    val viewModel : WatchViewModel by lazy { WatchViewModel.getInstance() }

    companion object {
        private const val TAG = "WatchDataLayerService"
        private const val TIMER_STATUS_PATH = "/timer_status"
        private const val TIMER_ACTION_PATH = "/timer_action"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: 데이터 받은")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path

                if (path == TIMER_STATUS_PATH) {

                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val timerName = dataMap.getString("timer_name", "")

                    // todo 이거 제거할 것
                    val intent = Intent("com.example.pre_capstone.TIMER_UPDATE")
                    intent.putExtra("timer_name", timerName)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//todo 앱에서 데이터 보내는것 규격화해서 보낼 것

                    viewModel.updateWatchInfo(
                        newData = WatchData(
                            timerSetting = TimerSetting(
                                name = timerName,

                            ),
                            activeTimer = activeStopwatch,

                            )
                    )





                    Log.d(TAG, "워치: 타이머 상태 수신: $timerName, 활성 타이머: $activeStopwatch")
                    Log.d(TAG, "워치: 타이머 맵: $dataMap")
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val path = messageEvent.path

        when (path) {
            TIMER_ACTION_PATH -> {
                val action = String(messageEvent.data)

                // 워치에서는 일반적으로 UI 업데이트를 위한 브로드캐스트 전송
                val intent = Intent("com.example.pre_capstone.TIMER_ACTION")
                intent.putExtra("action", action)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                Log.d(TAG, "워치: 타이머 액션 수신: $action")



            }
        }
    }
}

