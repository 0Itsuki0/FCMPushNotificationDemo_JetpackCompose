package com.example.fcmnotificationdemo.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fcmnotificationdemo.PushNotificationManager

enum class MainNavigationRoute {
    HOME,
    DETAIL
}


class MainNavigationViewModel: ViewModel() {
    lateinit var navController: NavHostController
    private var homeViewModel = HomeViewModel()
    private var detailViewModels: MutableList<DetailViewModel> = mutableListOf()

    fun isNavControllerInitialized(): Boolean {
        return this::navController.isInitialized
    }

    fun showPushNotification() {
        Log.d("show push notification", "")
        val newPushModel = DetailViewModel()
        newPushModel.setUpData(PushNotificationManager.getDataReceived())
        detailViewModels.add(newPushModel)

        navController.navigate(MainNavigationRoute.DETAIL.name) {
            restoreState = false
            launchSingleTop = false
        }
    }

    @Composable
    fun Run(){

        navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = MainNavigationRoute.HOME.name)
        {

            composable(MainNavigationRoute.HOME.name) {
                homeViewModel.Run()
            }

            composable(MainNavigationRoute.DETAIL.name) {
                if (detailViewModels.isEmpty()) {
                    return@composable
                }
                detailViewModels.last().Run(
                    navigateUp = {
                        navController.navigateUp()
                        detailViewModels.removeLast()
                    }
                )
            }
        }
    }
}