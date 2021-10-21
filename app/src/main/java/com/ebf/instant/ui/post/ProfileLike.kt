package com.ebf.instant.ui.post

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.ebf.instant.model.Like
import com.ebf.instant.model.LikeWithUser
import com.ebf.instant.model.User
import com.ebf.instant.ui.theme.InstantTheme
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfilesLikes(likes: List<LikeWithUser>, currentUser: User, isLiked: Boolean) {
    Box {
        likes.forEachIndexed { index, likeWithUser ->
            Row {
                Spacer(modifier = Modifier.width((index * 18).dp))
                ProfileLikeIcon(likeWithUser.user.imageUrl)
            }
        }
    }
}

@Composable
fun ProfileLikeIcon(
    profileImageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colors.surface,
            modifier = Modifier.size(28.dp),
            content = {}
        )
        Image(
            painter = rememberImagePainter(data = profileImageUrl, ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
        )
    }
}

@Preview
@Composable
fun ProfilesLikesPreview() {
    InstantTheme {
        ProfilesLikes(
            likes = listOf(
                LikeWithUser(
                    like = Like(id = "a", date = Date(), userId = "userId", postId = "postId"),
                    user = User(id = "userId", username = "vince.app", name = "Vincent", imageUrl = "url")
                ),
                LikeWithUser(
                    like = Like(id = "a", date = Date(), userId = "userId", postId = "postId"),
                    user = User(id = "userId2", username = "vince.app", name = "Vincent", imageUrl = "url")
                ),
            ),
            currentUser = User(id = "userId2", username = "vince.app", name = "Vincent", imageUrl = "url"),
            isLiked = false
        )
    }
}
