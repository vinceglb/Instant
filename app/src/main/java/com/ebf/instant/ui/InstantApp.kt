package com.ebf.instant.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.create.CreateAccount
import com.ebf.instant.ui.feed.FeedScreen
import com.ebf.instant.ui.login.LoginScreen
import com.ebf.instant.ui.signin.Ok
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
fun InstantApp(
    instantAppViewModel: InstantAppViewModel = getViewModel()
) {
    val userState by instantAppViewModel.userState.collectAsState()
    Timber.d("Current state is $userState")

    Crossfade(targetState = userState) { state ->
        when(state) {
            Ok.NOT_CONNECTED -> {
                LoginScreen()
            }
            Ok.LOGGED_NOT_VALID -> {
                CreateAccount()
            }
            Ok.LOGGED_VALID -> {
                InstantGraph()
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }


//    NavHost(
//        navController = navController,
//        startDestination = "launch"
//    ) {
//        // We launch the app in this section to let us determinate
//        // if the user is logged in or not (see Auth)
//        composable("launch") { }
//
//        // All the screens related to the on boarding section
//        loginGraph()
//
//        // All the screens of the app. Here, the user must be logged in.
//        appGraph(navController)
//    }
//
//    // Composable observing currentUser.
//    // It routes to the right screens according to the currentUser state
//    Auth(navController = navController)
}

@Composable
fun InstantGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            FeedScreen(
                navigateToPostComments = {  },
                navigateToCameraScreen = { navController.navigate("camera") }
            )
        }
        composable("camera") {
            CameraScreen()
        }
        composable(
            route = "account/{userId}",
            deepLinks = listOf(navDeepLink {
                uriPattern = "instant://account/{userId}"
            })
        ) {
            Text(text = "account")
        }
    }
}

//fun NavGraphBuilder.loginGraph() {
//    navigation(startDestination = "welcome", route = "login") {
//        composable("welcome") {
//            LoginScreen()
//        }
//        composable("create") {
//            CreateAccount()
//        }
//    }
//}
//
//fun NavGraphBuilder.appGraph(navController: NavController) {
//    navigation(startDestination = "home", route = "app") {
//        composable("home") {
//            Text(text = "Home")
////            FeedScreen { userId: String ->
////                navController.navigate("account/$userId")
////            }
//        }
//
//        composable(
//            route = "account/{userId}",
//            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
//            deepLinks = listOf(navDeepLink {
//                uriPattern = "instant://account/{userId}"
//            })
//        ) { backStackEntry: NavBackStackEntry ->
//            // `userId` should always be present.
//            // If that's not the case, fail crashing the app!
//            val userId = backStackEntry.arguments?.getString("userId")!!
//            Text(text = "Account $userId")
////            AccountScreen(userId = userId) {
////                navController.navigate("home")
////            }
//        }
//    }
//}
//
//@Composable
//fun Auth(
//    navController: NavController,
//    viewModel: InstantAppViewModel = getViewModel()
//) {
//    val userState by viewModel.userState.collectAsState()
//    val currentRoute = navController.currentBackStackEntry?.destination?.route
//
//    Timber.d("User State $userState")
//
//    when (userState) {
//        Ok.NOT_CONNECTED -> {
//            navController.navigate("login") {
//                navController.popBackStack()
//            }
//        }
//
//        Ok.LOGGED_NOT_VALID -> {
//            navController.navigate("create")
//        }
//
//        Ok.LOGGED_VALID -> {
//            navController.navigate("app") {
//                navController.popBackStack()
//            }
//        }
//
//        else -> { }
//    }
//}
