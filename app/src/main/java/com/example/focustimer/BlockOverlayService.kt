package com.example.focustimer

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.focustimer.Activity.MainActivity

class BlockOverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {
    private lateinit var overlayView: ComposeView
    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var savedStateRegistryController: SavedStateRegistryController

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        Log.i("BlockService", "onCreate: 감시시작")

        // 1. LifecycleRegistry 초기화
        lifecycleRegistry = LifecycleRegistry(this) // ※ 실제로는 아래처럼 작성:
        // lifecycleRegistry = LifecycleRegistry(this)

        // 2. SavedStateRegistryController 초기화 및 복구
        savedStateRegistryController = SavedStateRegistryController.create(this)
        savedStateRegistryController.performRestore(null)

        // 3. Lifecycle 상태 전환 (CREATED → RESUMED)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        // 4. ComposeView 설정
        overlayView = ComposeView(this)
        overlayView.setViewTreeLifecycleOwner(this)
        overlayView.setViewTreeSavedStateRegistryOwner(this)
        overlayView.setContent {
            BlockOverlayCompose(onClose = {
                try {
                    val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                    windowManager.removeView(overlayView)
                } catch (e: Exception) {
                    Log.e("BlockService", "오버레이 제거 실패", e)
                }
                // 2. 우리 앱(MainActivity) 실행
                    val intent = Intent(this@BlockOverlayService, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                android.os.Handler().postDelayed({
                    stopSelf()
                }, 1000) // 0.1초 딜레이

            }
            )
        }

        // 5. 오버레이 표시
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @Composable
    fun BlockOverlayCompose(onClose: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000))
                .clickable { /* 클릭 방지 */ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("이 앱은 차단되었습니다", color = Color.White, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onClose) { Text("닫기") }
            }
        }
    }
}