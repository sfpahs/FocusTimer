package com.example.focustimer

import android.app.Application
import android.content.Intent

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startWatchCommunication()
    }
    private fun startWatchCommunication(){
        val intent = Intent(this,ServiceWatchCommunication::class.java)
        startService(intent)
    }
    private fun stopWatchCommunication(){
        val intent = Intent(this,ServiceWatchCommunication::class.java)
        stopService(intent)
    }
    override fun onTerminate() {
        stopWatchCommunication()
        super.onTerminate()
    }


}