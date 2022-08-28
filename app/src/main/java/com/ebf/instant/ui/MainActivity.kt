package com.ebf.instant.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ebf.instant.ui.theme.InstantTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            InstantTheme {

                // System bars color
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                InstantApp()

            }
        }
    }
}

@Composable
fun ConnectWithGoogleButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text(text = "Connect")
    }
}

@Composable
fun SignOutButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "Sign out")
    }
}
