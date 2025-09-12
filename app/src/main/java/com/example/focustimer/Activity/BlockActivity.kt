package com.example.focustimer.Activity

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class BlockActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면이 잠금/홈 등 위에 항상 표시되도록 설정
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // 간단한 레이아웃 구성
        val textView = TextView(this).apply {
            text = "이 앱은 현재 차단되어 있습니다."
            textSize = 20f
            setPadding(40, 200, 40, 40)
        }
        val closeButton = Button(this).apply {
            text = "닫기"
            setOnClickListener { finish() }
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(android.graphics.Color.WHITE)
            addView(textView)
            addView(closeButton)
        }

        setContentView(layout)
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 무시 (원하면 비활성화)
        // super.onBackPressed() // 주석 처리하면 뒤로가기가 안 됨
    }
}