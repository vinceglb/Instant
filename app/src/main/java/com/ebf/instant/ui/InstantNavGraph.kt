package com.ebf.instant.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ebf.instant.ui.InstantScreen.*
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.post.FeedScreen

@Composable
fun InstantNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Feed.name
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Feed.name) {
            FeedScreen()
        }

        composable(Camera.name) {
            CameraScreen(onPostUploaded = { navController.navigate(Feed.name) })
        }

        composable(Account.name) {
            // TODO Account Screen
            Text(text = "A faire")
        }
    }
}
