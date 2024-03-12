package com.example.fcmnotificationdemo

import android.util.Log
import kotlinx.coroutines.delay


object PushNotificationManager {
    private var countReceived: Int = 0

    fun setDataReceived(count: Int) {
        this.countReceived = count
    }

    fun getDataReceived(): Int {
        return this.countReceived
    }


    suspend fun registerTokenOnServer(token: String) {
        Log.d("push notification", "register token on server: $token")
        delay(2000)
    }

}