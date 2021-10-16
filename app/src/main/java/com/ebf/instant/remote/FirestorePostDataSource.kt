package com.ebf.instant.remote

import com.ebf.instant.model.Post
import com.ebf.instant.model.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime

interface PostDataSource {
    suspend fun getAllPosts(): List<Post>
}

class FirestorePostDataSource(private val firestore: FirebaseFirestore) : PostDataSource {

    override suspend fun getAllPosts(): List<Post> {
        val snapshot = firestore
            .collection("posts")
            .get()
            .await()
        return snapshot.documents.map { parsePostItem(it) }
    }

    private fun parsePostItem(snapshot: DocumentSnapshot): Post = Post(
        id = snapshot.id,
        date = ZonedDateTime.now(),
        imageUrl = snapshot["urlImage"] as? String ?: "",
        user = User(
            id = "plop",
            username = snapshot["user"] as? String ?: ""
        )
    )

}