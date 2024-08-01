// BackgroundService.kt
package com.example.weatherapplication

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class BackgroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        // Perform background tasks here
        Log.d("BackgroundService", "Service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle background work here
        Log.d("BackgroundService", "Service running")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BackgroundService", "Service stopped")
    }
}
