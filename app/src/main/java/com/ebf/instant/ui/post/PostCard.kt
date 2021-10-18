package com.ebf.instant.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.ebf.instant.R
import com.ebf.instant.model.Post
import com.ebf.instant.model.User
import com.ebf.instant.ui.theme.InstantTheme
import java.util.*

@Composable
fun PostCard(
    post: Post,
    placeholder: Boolean = false
) {
    val painter = rememberImagePainter(
        data = post.imageUrl,
        builder = {
            crossfade(true)
            if (placeholder) placeholder(R.drawable.post_image_example)
        }
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(top = 8.dp, bottom = 4.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = post.user.imageUrl),
                contentDescription = "Image de profile",
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = post.user.username)
        }

        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = 4.dp,
            modifier = Modifier.padding(all = 4.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Image(
                    painter = painter,
                    contentDescription = "Image du post",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 20.dp, max = 500.dp)
                )
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
        imageUrl = "url",
        date = Date()
    )
    InstantTheme {
        PostCard(post = post, placeholder = true)
    }
}
