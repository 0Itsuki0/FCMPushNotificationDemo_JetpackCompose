package com.example.fcmnotificationdemo


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyFirebaseMessagingService() : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        ProcessLifecycleOwner.get().lifecycle.currentState.name
//        DefaultLifecycleObserver
        Log.d("Push notification", "Message received")

        if (remoteMessage.data.isEmpty() || remoteMessage.notification == null) {
            return
        }

        Log.d("Push notification", "Message data payload: ${remoteMessage.data}")

        val messageData = remoteMessage.data

        var count: Int? = null
        if( "count" in messageData )
            count = messageData["count"]?.toInt()

        if (count == null) {
            return
        }

        sendNotification(remoteMessage.notification!!, count)

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        Log.d("Push notification", "Token Refreshed ${token}")

        GlobalScope.launch {
            PushNotificationManager.registerTokenOnServer(token)
        }

    }

    private fun sendNotification(notification: RemoteMessage.Notification, count: Int) {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        intent.putExtra("count", count.toString())

        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val channelId = "FCMDemoChannel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(notification.body)
            )
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            notification.title,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager.createNotificationChannel(channel)

        val notificationId = System.currentTimeMillis().toInt()
        manager.notify(notificationId, builder.build())
    }
}