package com.ebf.instant.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.post.FeedScreen

object MainDestinations {
    const val FEED = "feed"
    const val CAMERA = "camera"
}

@Composable
fun InstantNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.FEED
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(MainDestinations.FEED) {
            FeedScreen()
        }

        composable(MainDestinations.CAMERA) {
            CameraScreen(onPostUploaded = { navController.navigate(MainDestinations.FEED) })
        }
    }
}
