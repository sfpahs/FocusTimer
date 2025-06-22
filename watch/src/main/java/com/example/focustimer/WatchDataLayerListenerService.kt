package com.example.focustimer
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.shared.model.Timer
import com.example.shared.model.TimerViewModel
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
class WatchDataLayerListenerService : WearableListenerService() {
    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }

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

                if (path == "/timer_status") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val watchData = decodeWatchData(dataMap)
                    Log.d(TAG, "onDataChanged: $dataMap")
                    watchData?.let {
                        // 복호화된 WatchData 객체 사용
                        viewModel.setTimer(
                            newData = Timer(
                                MySubject = it.MySubject,
                                activeTimer = it.activeTimer,
                                time = it.time
                                )
                        )
                        Log.d(TAG, "워치: 타이머: ${it.time}, 활성: ${it.activeTimer}")
                        Log.d(TAG, "워치: 타이머 맵: $dataMap")
                    }

                }
            }
        }
    }

    fun decodeWatchData(dataMap: DataMap): Timer? {
        try {
            // dataMap에서 JSON 문자열 가져오기
            val jsonWatchData = dataMap.getString("watch_data_json", "")

            // JSON 문자열이 비어있지 않은 경우에만 처리
            if (jsonWatchData.isNotEmpty()) {
                // Gson 인스턴스 생성
                val gson  = Gson()

                // JSON 문자열을 WatchData 객체로 역직렬화
                return gson.fromJson(jsonWatchData, Timer::class.java)
            }
            return null
        } catch (e: Exception) {
            Log.e("WatchDataDecoder", "워치 데이터 복호화 실패: ${e.message}")
            return null
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

