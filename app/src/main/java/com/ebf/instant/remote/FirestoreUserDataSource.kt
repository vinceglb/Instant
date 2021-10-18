package com.ebf.instant.remote

import com.ebf.instant.model.User
import com.ebf.instant.model.UserPartial
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface UserDataSource {
    suspend fun getUserById(userId: String): User
    suspend fun updateUserInfo(userPartial: UserPartial): User
}

class FirestoreUserDataSource(private val firestore: FirebaseFirestore) : UserDataSource {
    override suspend fun getUserById(userId: String): User {
        val snapshot = firestore
            .collection("usersInstant")
            .document(userId)
            .get()
            .await()
        return parseUserItem(snapshot = snapshot)
    }

    override suspend fun updateUserInfo(userPartial: UserPartial): User {
        firestore
            .collection("usersInstant")
            .document(userPartial.id)
            .set(userPartial)
            .await()
        return getUserById(userId = userPartial.id)
    }

    private fun parseUserItem(snapshot: DocumentSnapshot): User = User(
        id = snapshot.id,
        username = snapshot["username"] as? String? ?: "",
        name = snapshot["name"] as? String? ?: "",
        imageUrl = snapshot["imageUrl"] as? String? ?: ""
    )
}
