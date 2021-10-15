package com.ebf.instant.ui

import androidx.compose.runtime.*
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.login.LoginScreen
import com.ebf.instant.ui.theme.InstantTheme

@Composable
fun InstantApp() {
    InstantTheme {
        Auth()
    }
}

@Composable
fun Auth() {
    var userId by remember { mutableStateOf("") }
    if (userId.isEmpty()) {
        LoginScreen(onLoginSuccess = { uid -> userId = uid })
    } else {
        CameraScreen()
    }
}
