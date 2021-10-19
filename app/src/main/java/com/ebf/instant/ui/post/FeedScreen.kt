package com.ebf.instant.ui.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ebf.instant.model.PostWithUser
import org.koin.androidx.compose.getViewModel

@Composable
fun FeedScreen(viewModel: FeedScreenViewModel = getViewModel()) {
    val postWithUsers: List<PostWithUser> by viewModel.posts.collectAsState(initial = emptyList())
    LazyColumn {
        items(postWithUsers) { post ->
            PostCard(postWithUser = post)
        }
    }
}
