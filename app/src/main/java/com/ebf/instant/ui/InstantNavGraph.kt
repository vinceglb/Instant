package com.ebf.instant.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.login.LoginScreen

object MainDestinations {
    const val LOGIN = "login"
    const val CAMERA = "camera"
}

@Composable
fun InstantNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(MainDestinations.CAMERA)
                }
            )
        }

        composable(MainDestinations.CAMERA) {
            CameraScreen(Modifier.fillMaxSize())
        }
    }
}
