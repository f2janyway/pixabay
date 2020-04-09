package com.box.coroutinex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.box.coroutinex.MainActivity.Companion.job

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "cancel") {
            job!!.cancel(null)
        }

    }
}