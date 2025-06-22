package com.example.focustimer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import com.example.focustimer.BlockOverlayService


class AppBlockAccessibilityService : AccessibilityService() {
    private lateinit var allowedApps: Set<String>

    override fun onServiceConnected() {
        super.onServiceConnected()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        allowedApps = prefs.getStringSet("allowed_apps", emptySet()) ?: emptySet()
        Log.i("blockService", "onServiceConnected: 감시시작")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("stop_accessibility_service", false)) {
            // 동작을 멈춤 (예: 이벤트 무시)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return
        }
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.i("Block", "onAccessibilityEvent: 꺼야함")
            val packageName = event.packageName?.toString() ?: return
            if (!isAllowedApp(packageName)) {
                val intent = Intent(this, BlockOverlayService::class.java)
                startService(intent)
            }
        }
    }

    override fun onInterrupt() {}
    private fun isAllowedApp(packageName: String) = allowedApps.contains(packageName)
}
