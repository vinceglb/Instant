package com.ebf.instant.ui.feed

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun FeedScreen(
    navigateToPostComments: (String) -> Unit,
    navigateTo: (String) -> Unit,
    viewModel: FeedViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
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
        LazyColumn(state = listState, modifier = Modifier.padding(innerPadding)) {
            items(
                items = state.posts,
                key = { it.post.id }
            ) { post ->
                PostCard(
                    postWithData = post,
                    currentUserId = viewModel.userIdValue,
                    onLikeOrDislike = { postId -> viewModel.likeOrDislikePost(postId = postId) },
                    navigateToPostComments = navigateToPostComments
                )
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var oldList by remember { mutableStateOf(state.posts) }
    if (oldList != state.posts) {
        LaunchedEffect(key1 = state.posts) {
            oldList = state.posts
            coroutineScope.launch {
                listState.animateScrollToItem(index = 0)
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
