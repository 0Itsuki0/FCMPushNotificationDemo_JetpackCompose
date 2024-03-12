package com.example.fcmnotificationdemo.login

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fcmnotificationdemo.main.MainNavigationViewModel


enum class LoginNavigationRoute {
    LOGIN,
    MAIN
}

class RootNavigationViewModel: ViewModel() {
    lateinit var navController: NavHostController

    private var mainNavigationViewModel = MainNavigationViewModel()
    private var loginViewModel = LoginViewModel()

    fun getMainNavigationViewModel(): MainNavigationViewModel? {
        if (!this::navController.isInitialized) {
            return null
        }
        val backStackEntry = navController.currentBackStackEntry
        if (backStackEntry?.destination?.route == LoginNavigationRoute.MAIN.name) {
            if (mainNavigationViewModel.isNavControllerInitialized() ) {
                return mainNavigationViewModel
            }
            return null
        }
        return null
    }

    @Composable
    fun Run() {
        navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = LoginNavigationRoute.LOGIN.name)
        {

            composable(LoginNavigationRoute.LOGIN.name) {
                loginViewModel.Run(
                    onLoginPressed = { navController.navigate(LoginNavigationRoute.MAIN.name) }
                )
            }

            composable(LoginNavigationRoute.MAIN.name) {
                mainNavigationViewModel.Run()
            }
        }
    }
}