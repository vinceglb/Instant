package com.ebf.instant.remote

import com.ebf.instant.model.PostWithUser
import com.ebf.instant.model.Post
import com.ebf.instant.model.PostToPublish
import com.ebf.instant.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface PostDataSource {
    suspend fun publishPost(postToPublish: PostToPublish)
    suspend fun getAllPosts(): List<PostWithUser>
}

class FirestorePostDataSource(private val firestore: FirebaseFirestore) : PostDataSource {

    override suspend fun publishPost(postToPublish: PostToPublish) {
        val data = mapOf(
            IMAGE_URL to postToPublish.imageUrl,
            TIMESTAMP to Timestamp.now(),
            USER to mapOf(
                USER_ID to postToPublish.user.id,
                USER_IMAGE_URL to postToPublish.user.imageUrl,
                USER_USERNAME to postToPublish.user.username,
                USER_NAME to postToPublish.user.name
            )
        )

        firestore
            .collection("posts")
            .add(data)
            .await()
    }

    override suspend fun getAllPosts(): List<PostWithUser> {
        val snapshot = firestore
            .collection("posts")
            .get()
            .await()
        return snapshot.documents.map { parsePostItem(it) }
    }

    private fun parsePostItem(snapshot: DocumentSnapshot): PostWithUser = PostWithUser(
        post = Post(
            id = snapshot.id,
            date = (snapshot[TIMESTAMP] as? Timestamp ?: Timestamp.now()).toDate(),
            imageUrl = snapshot[IMAGE_URL] as? String ?: "",
            userId = snapshot["$USER.$USER_ID"] as? String ?: ""
        ),
        user = User(
            id = snapshot["$USER.$USER_ID"] as? String ?: "",
            username = snapshot["$USER.$USER_USERNAME"] as? String ?: "",
            name = snapshot["$USER.$USER_NAME"] as? String ?: "",
            imageUrl = snapshot["$USER.$IMAGE_URL"] as? String ?: "",
        )
    )

    companion object {
        private const val TIMESTAMP = "timestamp"
        private const val IMAGE_URL = "imageUrl"
        private const val USER = "user"

        private const val USER_ID = "id"
        private const val USER_USERNAME = "username"
        private const val USER_NAME = "name"
        private const val USER_IMAGE_URL = "imageUrl"
    }

}