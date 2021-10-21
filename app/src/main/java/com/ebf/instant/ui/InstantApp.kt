package com.ebf.instant.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ebf.instant.model.User
import com.ebf.instant.ui.onboarding.WelcomeScreen
import com.ebf.instant.ui.theme.InstantTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun InstantApp() {
    InstantTheme {

        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }

        Auth()
    }
}

@Composable
fun Auth() {
    // var userId by remember { mutableStateOf(Firebase.auth.currentUser?.uid ?: "") }
    // if (userId.isEmpty()) {
    // LoginScreen(onLoginSuccess = { uid -> userId = uid })
    WelcomeScreen()
//    } else {
//        AppContainer()
//    }
}

@Composable
fun AppContainer(currentUser: User) {
    val allScreens = listOf(InstantScreen.Feed, InstantScreen.Camera, InstantScreen.Account)
    val navController = rememberNavController()
    val backstackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = InstantScreen.fromRoute(backstackEntry.value?.destination?.route)

    Scaffold(
        bottomBar = {
            InstantBottomAppBar(
                allScreens = allScreens,
                onScreenSelected = { navController.navigate(it.name) },
                currentScreen = currentScreen
            )
        }
    ) { innerPadding ->
        InstantNavGraph(
            currentUser = currentUser,
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}

@Composable
fun InstantBottomAppBar(
    allScreens: List<InstantScreen>,
    onScreenSelected: (InstantScreen) -> Unit,
    currentScreen: InstantScreen
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        allScreens.forEach { screen ->
            BottomNavigationItem(
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.label) },
                label = { Text(text = screen.label) },
                alwaysShowLabel = false
            )
        }
    }
}
