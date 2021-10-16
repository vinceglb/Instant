package com.ebf.instant.ui.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ebf.instant.model.Post
import org.koin.androidx.compose.getViewModel

@Composable
fun FeedScreen(viewModel: FeedScreenViewModel = getViewModel()) {
    val posts: List<Post> by viewModel.posts.collectAsState(initial = emptyList())
    LazyColumn {
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}
