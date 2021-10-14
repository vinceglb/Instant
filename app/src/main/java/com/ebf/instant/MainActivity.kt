package com.ebf.instant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.theme.InstantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstantTheme {
                CameraScreen()
                // LoginScreen()

            }
        }
    }
}
