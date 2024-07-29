package com.example.assignment5.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.assignment5.core.service.PlaybackService

class ServiceReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "ServiceReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val actionMusic = intent?.getIntExtra("action_music", PlaybackService.ACTION_PLAY)
        Log.d(TAG, "onReceive: $actionMusic")
        val serviceIntent = Intent(context, PlaybackService::class.java)
        serviceIntent.putExtra("action_music_service", actionMusic)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(serviceIntent)
        } else {
            context?.startService(serviceIntent)
        }
    }
}