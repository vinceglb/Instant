package com.ebf.instant.ui.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.getViewModel

@Composable
fun FeedScreen(
    navigateToPostComments: (String) -> Unit,
    navigateToCameraScreen: () -> Unit,
    viewModel: FeedViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn {
        item {
            Button(onClick = navigateToCameraScreen) {
                Text(text = "Camera screen")
            }
        }
        items(state.posts) { post ->
            PostCard(
                postWithData = post,
                currentUserId = viewModel.userIdValue,
                onLikeOrDislike = {
                    viewModel.likeOrDislikePost(postId = post.post.id)
                },
                navigateToPostComments = navigateToPostComments
            )
        }
    }
}
