package com.ebf.instant.ui.feed

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.androidx.compose.getViewModel

@Composable
fun FeedScreen(
    navigateToPostComments: (String) -> Unit,
    navigateTo: (String) -> Unit,
    viewModel: FeedViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()
    val allScreens = listOf(
        InstantScreen(label = "home", icon = Icons.Rounded.Home),
        InstantScreen(label = "camera", icon = Icons.Rounded.AddBox),
        InstantScreen(label = "account", icon = Icons.Rounded.AccountCircle)
    )

    Scaffold(
        bottomBar = {
            InstantBottomAppBar(
                allScreens = allScreens,
                onScreenSelected = { navigateTo(it.label) },
                currentScreen = allScreens.first()
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(state.posts) { post ->
                PostCard(
                    postWithData = post,
                    currentUserId = viewModel.userIdValue,
                    onLikeOrDislike = { postId ->
                        viewModel.likeOrDislikePost(postId = postId)
                    },
                    navigateToPostComments = navigateToPostComments
                )
            }
        }
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

data class InstantScreen(
    val label: String,
    val icon: ImageVector,
)
