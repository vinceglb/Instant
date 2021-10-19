package com.ebf.instant.ui.post

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.ebf.instant.R
import com.ebf.instant.model.PostWithUser
import com.ebf.instant.model.Post
import com.ebf.instant.model.User
import com.ebf.instant.ui.theme.InstantTheme
import java.util.*

enum class BounceState { Pressed, Released }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostCard(
    postWithUser: PostWithUser,
    preview: Boolean = false
) {

    var isLiked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painter(url = postWithUser.user.imageUrl, tool = R.drawable.profile_pitcure, preview = preview),
                contentDescription = "Image de profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = postWithUser.user.username,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Il y a 18 minutes",
                    style = MaterialTheme.typography.caption,
                )
            }
        }

        Text(
            text = "Aujourd'hui, on a fait une super sortie en forêt et c'était top !",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        PostCardImage(postWithUser = postWithUser, isLiked = isLiked, preview = preview) { isLiked = !isLiked }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Like
            LikeButton(isLiked = isLiked) { isLiked = !isLiked }

            // Comment
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = "Comment icon")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Profile picture images to represent likes
            ProfilesLikes()
        }

        Surface(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("romain.g")
                    }
                    append(" ")
                    append("Wow ça à l'air super beau !!")
                }, modifier = Modifier.padding(vertical = 2.dp))
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("sophy.algs")
                    }
                    append(" ")
                    append("On a passé un super moment.")
                }, modifier = Modifier.padding(vertical = 1.dp))
            }
        }

    }
}

@Composable
fun PostCardImage(postWithUser: PostWithUser, isLiked: Boolean, preview: Boolean, onDoubleTap: () -> Unit) {
    var currentState: BounceState by remember { mutableStateOf(BounceState.Released) }

    val scale by animateFloatAsState(
        targetValue = if (currentState == BounceState.Pressed) 1.03f else 1f,
        finishedListener = { currentState = BounceState.Released },
        animationSpec = spring(dampingRatio = if (currentState == BounceState.Released) Spring.DampingRatioMediumBouncy else Spring.DampingRatioNoBouncy)
    )

    val cardElevation by animateDpAsState(targetValue = if (isLiked) 8.dp else 0.dp)

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = cardElevation,
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        onDoubleTap()
                        currentState = BounceState.Pressed
                    }
                )
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Image(
                painter = painter(postWithUser.post.imageUrl, R.drawable.post_image_example, preview = preview),
                contentDescription = "Image du post",
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

@Composable
fun ProfilesLikes() {
    Box {
        ProfileLikeIcon(R.drawable.profile_pitcure)
        Row {
            Spacer(modifier = Modifier.width(18.dp))
            ProfileLikeIcon(R.drawable.profile_pitcure)
        }
    }
}

@Composable
fun ProfileLikeIcon(
    @DrawableRes profileImage: Int,
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
            painter = painterResource(id = profileImage),
            contentDescription = null,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
        )
    }
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

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    val post = PostWithUser(
        post = Post(
            id = "test",
            imageUrl = "",
            date = Date(),
            userId = "test"
        ),
        user = User(id = "test", username = "vince.app", name = "Vincent", ""),
    )
    InstantTheme {
        PostCard(postWithUser = post, preview = true)
    }
}
