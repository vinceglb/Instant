package com.ebf.instant.data.post

import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.model.LikeWithUser
import com.ebf.instant.model.PostWithData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestorePostDataSource(
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun fetchAllPosts(): List<PostWithData> = withContext(ioDispatcher) {
        val snapshot = firestore
            .collection("posts")
            .get()
            .await()

        snapshot.documents.map { doc ->
            val likes = getLikesFromPost(doc.id)
            val comments = getCommentsFromPost(doc.id)
            parsePostItem(snapshot = doc, likes = likes, comments = comments)
        }
    }


    private suspend fun getCommentsFromPost(postId: String): List<CommentWithUser> =
        withContext(ioDispatcher) {
            val snapshot = firestore
                .collection("posts")
                .document(postId)
                .collection("comments")
                .get()
                .await()

            snapshot.documents.map { parseCommentItem(snapshot = it, postId = postId) }
        }

    private suspend fun getLikesFromPost(postId: String): List<LikeWithUser> =
        withContext(ioDispatcher) {
            val snapshot = firestore
                .collection("posts")
                .document(postId)
                .collection("likes")
                .get()
                .await()

            snapshot.documents.map { parseLikeItem(snapshot = it, postId = postId) }
        }


    companion object {
        const val POST_TIMESTAMP = "createTimestamp"
        const val POST_IMAGE_URL = "imageUrl"
        const val POST_USER = "user"
        const val POST_DESCRIPTION = "description"

        const val USER_ID = "id"
        const val USER_USERNAME = "username"
        const val USER_NAME = "name"
        const val USER_IMAGE_URL = "imageUrl"

        const val COMMENT_CONTENT = "comment_content"
        const val COMMENT_TIMESTAMP = "comment_timestamp"
        const val COMMENT_USER = "comment_user"

        const val LIKE_TIMESTAMP = "timestamp"
        const val LIKE_USER = "user"
    }

}