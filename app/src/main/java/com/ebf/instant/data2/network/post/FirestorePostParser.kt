package com.ebf.instant.data2.network.post

import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.COMMENT_CONTENT
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.COMMENT_TIMESTAMP
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.COMMENT_USER
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.LIKE_TIMESTAMP
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.LIKE_USER
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.POST_DESCRIPTION
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.POST_IMAGE_URL
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.POST_TIMESTAMP
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.POST_USER
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.USER_ID
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.USER_IMAGE_URL
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.USER_NAME
import com.ebf.instant.data2.network.post.FirestorePostDataSource.Companion.USER_USERNAME
import com.ebf.instant.model.Comment
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.model.Like
import com.ebf.instant.model.LikeWithUser
import com.ebf.instant.model.Post
import com.ebf.instant.model.PostWithData
import com.ebf.instant.model.User
import com.ebf.instant.util.castOrDefault
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

fun parsePostItem(
    snapshot: DocumentSnapshot,
    likes: List<LikeWithUser>,
    comments: List<CommentWithUser>
): PostWithData = PostWithData(
    post = Post(
        id = snapshot.id,
        date = snapshot[POST_TIMESTAMP].castOrDefault(Timestamp.now()).toDate(),
        imageUrl = snapshot[POST_IMAGE_URL].castOrDefault(""),
        userId = snapshot["$POST_USER.$USER_ID"].castOrDefault(""),
        description = snapshot[POST_DESCRIPTION] as String?
    ),
    user = User(
        id = snapshot["$POST_USER.$USER_ID"].castOrDefault(""),
        username = snapshot["$POST_USER.$USER_USERNAME"].castOrDefault(""),
        name = snapshot["$POST_USER.$USER_NAME"].castOrDefault(""),
        imageUrl = snapshot["$POST_USER.$USER_IMAGE_URL"].castOrDefault(""),
    ),
    comments = comments,
    likes = likes
)

fun parseCommentItem(snapshot: DocumentSnapshot, postId: String): CommentWithUser =
    CommentWithUser(
        comment = Comment(
            id = snapshot.id,
            content = snapshot[COMMENT_CONTENT].castOrDefault(""),
            createDate = snapshot[COMMENT_TIMESTAMP].castOrDefault(Timestamp.now()).toDate(),
            userId = snapshot["$COMMENT_USER.$USER_ID"].castOrDefault(""),
            postId = postId
        ),
        user = User(
            id = snapshot["$COMMENT_USER.$USER_ID"].castOrDefault(""),
            username = snapshot["$COMMENT_USER.$USER_USERNAME"].castOrDefault(""),
            name = snapshot["$COMMENT_USER.$USER_NAME"].castOrDefault(""),
            imageUrl = snapshot["$COMMENT_USER.$USER_IMAGE_URL"].castOrDefault(""),
        )
    )

fun parseLikeItem(snapshot: DocumentSnapshot, postId: String): LikeWithUser =
    LikeWithUser(
        like = Like(
            id = "$postId-${snapshot["$LIKE_USER.$USER_ID"].castOrDefault("")}",
            date = snapshot[LIKE_TIMESTAMP].castOrDefault(Timestamp.now()).toDate(),
            userId = snapshot["$LIKE_USER.$USER_ID"].castOrDefault(""),
            postId = postId
        ),
        user = User(
            id = snapshot["$LIKE_USER.$USER_ID"].castOrDefault(""),
            username = snapshot["$LIKE_USER.$USER_USERNAME"].castOrDefault(""),
            name = snapshot["$LIKE_USER.$USER_NAME"].castOrDefault(""),
            imageUrl = snapshot["$LIKE_USER.$USER_IMAGE_URL"].castOrDefault(""),
        )
    )