package com.ebf.instant.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ebf.instant.model.User
import com.ebf.instant.ui.InstantScreen.*
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.post.FeedScreen
import com.ebf.instant.ui.post.comment.CommentScreen

@Composable
fun InstantNavGraph(
    currentUser: User,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Feed.name
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Feed.name) {
            FeedScreen(
                currentUser = currentUser,
                navigateToPostComments = actions.navigateToComments
            )
        }

        composable(Camera.name) {
            CameraScreen(onPostUploaded = { navController.navigate(Feed.name) })
        }

        composable(Account.name) {
            // TODO Account Screen
            Text(text = "TODO")
        }

        composable(
            route = "${Comments.name}/{postId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            )
        ) { entry ->
            val postId: String = entry.arguments?.getString("postId")!!

            CommentScreen(
                postId = postId,
                onBack = actions.upPress
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToComments: (String) -> Unit = { postId: String ->
        navController.navigate("${Comments.name}/$postId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
