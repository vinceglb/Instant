package com.ebf.instant.ui.post

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import com.ebf.instant.model.Post
import com.ebf.instant.model.User
import com.ebf.instant.ui.theme.InstantTheme
import java.util.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun PostCard(
    post: Post,
    preview: Boolean = false
) {
    @Composable
    fun painter(url: String, @DrawableRes tool: Int): ImagePainter =
        rememberImagePainter(
            data = url,
            builder = {
                crossfade(true)
                if (preview) placeholder(tool)
            }
        )

    var isLiked by remember { mutableStateOf(false) }
    val cardElevation by animateDpAsState(targetValue = if (isLiked) 8.dp else 0.dp)

    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,) {
            Image(
                painter = painter(url = post.user.imageUrl, tool = R.drawable.profile_pitcure),
                contentDescription = "Image de profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = post.user.username, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
        }

        Text(
            text = "Aujourd'hui, on a fait une super sortie en forêt et c'était top !",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = cardElevation,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            isLiked = !isLiked

                        }
                    )
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Image(
                    painter = painter(url = post.imageUrl, tool = R.drawable.post_image_example),
                    contentDescription = "Image du post",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 20.dp, max = 500.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Like
            LikeButton(isLiked = isLiked) { isLiked = !isLiked }

            // Comment
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = "Comment icon")
            }

            Spacer(modifier = Modifier.weight(1f))

//            Text(text = "14 Likes", modifier = Modifier.padding(end = 8.dp))
            Box {
                Image(
                    painter = painterResource(id = R.drawable.profile_pitcure),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(width = 4.dp, MaterialTheme.colors.surface, shape = CircleShape)
                )
            }
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

        Text(
            text = "Il y a 18 minutes",
            style = MaterialTheme.typography.overline,
            modifier = Modifier.padding(top = 8.dp)
        )

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

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    val post = Post(
        id = "test",
        user = User(id = "test", username = "vince.app", name = "Vincent", ""),
        imageUrl = "",
        date = Date()
    )
    InstantTheme {
        PostCard(post = post, preview = true)
    }
}
