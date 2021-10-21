package com.ebf.instant.ui.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ebf.instant.model.PostWithData
import com.ebf.instant.model.User
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun FeedScreen(
    currentUser: User,
    navigateToPostComments: (String) -> Unit,
    viewModel: FeedScreenViewModel = getViewModel()
) {
    val auth = get<FirebaseAuth>()
    val list: List<PostWithData> by viewModel.posts.collectAsState(initial = emptyList())

    LazyColumn {
        items(list) { post ->

            // We are looking for the user like if exists
            val isLiked = post.likes.find { it.user.id == auth.currentUser?.uid } != null

            PostCard(
                postWithData = post,
                isLiked = isLiked,
                onLikeOrDislike = {
                    viewModel.likeOrDislikePost(postId = post.post.id)
                },
                currentUser = currentUser,
                navigateToPostComments = navigateToPostComments
            )
        }
    }
}
