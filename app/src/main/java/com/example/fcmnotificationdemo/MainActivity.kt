package com.example.fcmnotificationdemo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fcmnotificationdemo.login.RootNavigationViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var rootNavigationViewModel = RootNavigationViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        askNotificationPermission()
        retrieveFCMToken()

        setContent {
            rootNavigationViewModel.Run()
        }

        // notification comes when app is killed
        val count: Int? = intent?.extras?.getString("count")?.toInt()
        Log.d("push notification", "on create count : $count")
        count?.let {
            PushNotificationManager.setDataReceived(count = count)
            lifecycleScope.launch {
                while (rootNavigationViewModel.getMainNavigationViewModel() == null) {
                    Log.d("push notification", "not logged in, waiting...")
                    delay(500)
                }
                val mainNavigationController = rootNavigationViewModel.getMainNavigationViewModel()
                mainNavigationController!!.showPushNotification()
                return@launch
            }
            return
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d("push notification ", " on new intent extras? : ${intent?.extras}")

        // notification coming when app in inactive/background, data included in intent extra
        val count: Int? = intent?.extras?.getString("count")?.toInt()
        count?.let {
            Log.d("push notification ", " on new intent count : $count")
            PushNotificationManager.setDataReceived(count = count)
            lifecycleScope.launch {

                while (rootNavigationViewModel.getMainNavigationViewModel() == null) {
                    Log.d("push notification", "not logged in, waiting...")
                    delay(100)
                }

                val mainNavigationController = rootNavigationViewModel.getMainNavigationViewModel()
                mainNavigationController!!.showPushNotification()
                return@launch
            }
            return
        }
    }

    private fun retrieveFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("push notification device token", "failed with error: ${task.exception}")
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("push notification device token", "token received: $token")
            lifecycleScope.launch {
                PushNotificationManager.registerTokenOnServer(token)
            }
        })
    }


    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        }
    }

    private fun askNotificationPermission() {
        // API33以降では権限の付与をユーザーへ聞く
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                return
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}
