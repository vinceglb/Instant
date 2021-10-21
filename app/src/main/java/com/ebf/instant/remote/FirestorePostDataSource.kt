package com.ebf.instant.remote

import com.ebf.instant.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface PostDataSource {
    suspend fun publishPost(postToPublish: PostToPublish)
    suspend fun commentPost(commentWithUser: CommentWithUser): String
    suspend fun likePost(likeWithUser: LikeWithUser)
    suspend fun dislikePost(postId: String, userId: String)
    suspend fun getAllPosts(): List<PostWithData>
    suspend fun getCommentsFromPost(postId: String): List<CommentWithUser>
    suspend fun getLikesFromPost(postId: String): List<LikeWithUser>
}

class FirestorePostDataSource(private val firestore: FirebaseFirestore) : PostDataSource {

    override suspend fun publishPost(postToPublish: PostToPublish) {
        val data = mapOf(
            POST_IMAGE_URL to postToPublish.imageUrl,
            POST_TIMESTAMP to Timestamp.now(),
            POST_USER to userToMap(postToPublish.user)
        )

        firestore
            .collection("posts")
            .add(data)
            .await()
    }

    override suspend fun commentPost(commentWithUser: CommentWithUser): String {
        val comment = mapOf(
            COMMENT_CONTENT to commentWithUser.comment.content,
            COMMENT_TIMESTAMP to Timestamp.now(),
            COMMENT_USER to userToMap(commentWithUser.user)
        )

        return firestore
            .collection("posts")
            .document(commentWithUser.comment.postId)
            .collection("comments")
            .add(comment)
            .await()
            .id
    }

    override suspend fun likePost(likeWithUser: LikeWithUser) {
        val like = mapOf(
            LIKE_TIMESTAMP to Timestamp.now(),
            LIKE_USER to likeWithUser.user
        )

        firestore
            .collection("posts")
            .document(likeWithUser.like.postId)
            .collection("likes")
            .document(likeWithUser.user.id)
            .set(like)
            .await()
    }

    override suspend fun dislikePost(postId: String, userId: String) {
        firestore
            .collection("posts")
            .document(postId)
            .collection("likes")
            .document(userId)
            .delete()
            .await()
    }

    override suspend fun getAllPosts(): List<PostWithData> {
        val snapshot = firestore
            .collection("posts")
            .get()
            .await()
        return snapshot.documents.map { parsePostItem(it) }
    }

    override suspend fun getCommentsFromPost(postId: String): List<CommentWithUser> {
        val snapshot = firestore
            .collection("posts")
            .document(postId)
            .collection("comments")
            .get()
            .await()
        return snapshot.documents.map { parseCommentItem(snapshot = it, postId = postId) }
    }

    override suspend fun getLikesFromPost(postId: String): List<LikeWithUser> {
        val snapshot = firestore
            .collection("posts")
            .document(postId)
            .collection("likes")
            .get()
            .await()
        return snapshot.documents.map { parseLikeItem(snapshot = it, postId = postId) }
    }

    private suspend fun parsePostItem(snapshot: DocumentSnapshot): PostWithData = PostWithData(
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
            imageUrl = snapshot["$POST_USER.$POST_IMAGE_URL"].castOrDefault(""),
        ),
        comments = getCommentsFromPost(snapshot.id),
        likes = getLikesFromPost(snapshot.id)
    )

    private fun parseCommentItem(snapshot: DocumentSnapshot, postId: String): CommentWithUser =
        CommentWithUser(
            comment = Comment(
                id = snapshot.id,
                content = snapshot[COMMENT_CONTENT].castOrDefault(""),
                date = snapshot[COMMENT_TIMESTAMP].castOrDefault(Timestamp.now()).toDate(),
                userId = snapshot["$COMMENT_USER.$USER_ID"].castOrDefault(""),
                postId = postId
            ),
            user = User(
                id = snapshot["$COMMENT_USER.$USER_ID"].castOrDefault(""),
                username = snapshot["$COMMENT_USER.$USER_USERNAME"].castOrDefault(""),
                name = snapshot["$COMMENT_USER.$USER_NAME"].castOrDefault(""),
                imageUrl = snapshot["$COMMENT_USER.$POST_IMAGE_URL"].castOrDefault(""),
            )
        )

    private fun parseLikeItem(snapshot: DocumentSnapshot, postId: String): LikeWithUser =
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
                imageUrl = snapshot["$LIKE_USER.$POST_IMAGE_URL"].castOrDefault(""),
            )
        )

    private fun userToMap(user: User): Map<String, String> =
        mapOf(
            USER_ID to user.id,
            USER_IMAGE_URL to user.imageUrl,
            USER_USERNAME to user.username,
            USER_NAME to user.name
        )

    @Suppress("UNCHECKED_CAST")
    private fun <T> Any?.castOrDefault(default: T): T = this as? T ?: default

    companion object {
        private const val POST_TIMESTAMP = "timestamp"
        private const val POST_IMAGE_URL = "imageUrl"
        private const val POST_USER = "user"
        private const val POST_DESCRIPTION = "description"

        private const val USER_ID = "id"
        private const val USER_USERNAME = "username"
        private const val USER_NAME = "name"
        private const val USER_IMAGE_URL = "imageUrl"

        private const val COMMENT_CONTENT = "comment_content"
        private const val COMMENT_TIMESTAMP = "comment_timestamp"
        private const val COMMENT_USER = "comment_user"

        private const val LIKE_TIMESTAMP = "timestamp"
        private const val LIKE_USER = "user"
    }

}