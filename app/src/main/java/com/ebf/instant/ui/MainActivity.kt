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



//@Composable
//fun FeedScreen(
//    vm: InstantAppViewModel = getViewModel(),
//    navigateToAccount: (String) -> Unit
//) {
//    val auth: FirebaseAuth = get()
//    val user by vm.userInfo.collectAsState()
//
//    Column {
//        Text(text = "Feed")
//
//        val isSignedIn = user?.isSignedIn()
//        if (isSignedIn == null) {
//            CircularProgressIndicator()
//        } else {
//            Text(text = isSignedIn.toString())
//            SignOutButton {
//                auth.signOut()
//            }
//            Button(onClick = { navigateToAccount(user?.getUid() ?: "") }) {
//                Text(text = "Go to Account ${user?.getUid()}")
//            }
//        }
//    }
//}
//
//@Composable
//fun AccountScreen(userId: String, navigateToFeed: () -> Unit) {
//    Column {
//        Text(text = userId)
//        Button(onClick = navigateToFeed) {
//            Text(text = "Go To Feed")
//        }
//    }
//}

//@Composable
//fun LoginScreen(vm: InstantAppViewModel = getViewModel()) {
//    val googleLoginLauncher = rememberLauncherForActivityResult(LoginWithGoogle()) { token ->
//        token?.let { vm.connectWithGoogle(it) }
//    }
//
//    Column {
//        Text(text = "LoginScreen")
//
//        ConnectWithGoogleButton {
//            googleLoginLauncher.launch()
//        }
//    }
//}

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
