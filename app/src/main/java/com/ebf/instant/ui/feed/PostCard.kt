package com.ebf.instant.ui.feed

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.ebf.instant.R
import com.ebf.instant.model.Comment
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.model.Post
import com.ebf.instant.model.PostWithData
import com.ebf.instant.model.User
import com.ebf.instant.ui.theme.InstantTheme
import org.ocpsoft.prettytime.PrettyTime
import java.util.Date

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostCard(
    postWithData: PostWithData,
    currentUserId: String?,
    navigateToPostComments: (String) -> Unit,
    onLikeOrDislike: (String) -> Unit,
    preview: Boolean = false
) {
    val prettyTime = remember { PrettyTime() }

    // We are looking for the user like if exists
    val isLiked = postWithData.likes.find { it.user.id == currentUserId } != null

    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painter(
                    url = postWithData.user.imageUrl,
                    tool = R.drawable.profile_pitcure,
                    preview = preview
                ),
                contentDescription = "Image de profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = postWithData.user.username,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = prettyTime.format(postWithData.post.date),
                    style = MaterialTheme.typography.caption,
                )
            }
        }

        postWithData.post.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } ?: Spacer(modifier = Modifier.height(16.dp))

        PostCardImage(
            postWithData = postWithData,
            isLiked = isLiked,
            preview = preview
        ) {
            onLikeOrDislike(postWithData.post.id)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Like
            LikeButton(isLiked = isLiked) {
                onLikeOrDislike(postWithData.post.id)
            }

            // Comment
            IconButton(onClick = { navigateToPostComments(postWithData.post.id) }) {
                Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = "Comment icon")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Profile picture images to represent likes
            ProfilesLikes(likes = postWithData.likes)
        }

        Surface(
            onClick = { navigateToPostComments(postWithData.post.id) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                postWithData.comments.forEach { commentWithUser ->
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(commentWithUser.user.username)
                        }
                        append(" ")
                        append(commentWithUser.comment.content)
                    }, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
fun PostCardImage(
    postWithData: PostWithData,
    isLiked: Boolean,
    preview: Boolean,
    onDoubleTap: (Boolean) -> Unit
) {
    val currentState = if (isLiked) LikeState.Liked else LikeState.Unliked
    val transition = updateTransition(targetState = currentState, "Like transition")
    val elevation by transition.animateDp(label = "Card elevation") { state ->
        when (state) {
            LikeState.Unliked -> 0.dp
            LikeState.Liked -> 8.dp
        }
    }
    val scale by transition.animateFloat(
        label = "Card scale",
        transitionSpec = {
            when {
                LikeState.Unliked isTransitioningTo LikeState.Liked -> keyframes {
                    durationMillis = 350
                    0.99f at 100
                    1.03f at 220
                }
                else -> tween()
            }
        }
    ) { state ->
        when (state) {
            LikeState.Unliked -> 1f
            LikeState.Liked -> 1f
        }
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = elevation,
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        onDoubleTap(isLiked)
                    }
                )
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Image(
                painter = painter(
                    postWithData.post.imageUrl,
                    R.drawable.post_image_example,
                    preview = preview
                ),
                contentDescription = "Post's image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 20.dp, max = 500.dp)
            )
        }
    }
}

@Composable
fun LikeButton(isLiked: Boolean, onClick: () -> Unit) {
    Crossfade(targetState = isLiked) { liked ->
        if (liked) {
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "Unlike icon",
                    tint = MaterialTheme.colors.primary
                )
            }
        } else {
            IconButton(onClick = onClick) {
                Icon(Icons.Rounded.FavoriteBorder, contentDescription = "Like icon")
            }
        }
    }
}

enum class LikeState {
    Liked,
    Unliked
}

@Composable
fun painter(url: String, @DrawableRes tool: Int, preview: Boolean): ImagePainter =
    rememberImagePainter(
        data = url,
        builder = {
            crossfade(true)
            if (preview) placeholder(tool)
        }
    )

@Preview
@Composable
fun PostCardImagePreview() {
    InstantTheme {
        PostCardImage(postWithData = fakePost, isLiked = false, preview = true) {}
    }
}

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    InstantTheme {
        PostCard(
            postWithData = fakePost,
            preview = true,
            currentUserId = "uid",
            onLikeOrDislike = {},
            navigateToPostComments = {})
    }
}

private val fakePost = PostWithData(
    post = Post(
        id = "test",
        imageUrl = "",
        date = Date(),
        userId = "test",
        description = "Today, we walk all day long in the mountain and it was awesome !"
    ),
    user = User(id = "test", username = "vince.app", name = "Vincent", ""),
    comments = listOf(
        CommentWithUser(
            comment = Comment("", Date(), "Yo mec comment ça va ?", "", ""),
            user = User("", "sophy.algs", "Vince", "")
        ),
        CommentWithUser(
            comment = Comment("", Date(), "Trop beau !", "", ""),
            user = User("", "romain.glb", "Vince", "")
        )
    ),
    likes = listOf()
)