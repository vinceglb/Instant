package com.ebf.instant.data.user

import com.ebf.instant.Result
import com.ebf.instant.data.signin.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreUserDataSource(
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun updateUserInfo(
        userId: String,
        userInfo: UserInfo
    ): Result<UserInfo> = withContext(ioDispatcher) {

        val data = mapOf(
            "name" to userInfo.name,
            "username" to userInfo.username,
            "imageUrl" to userInfo.imageUrl
        )

        firestore
            .collection("users")
            .document(userId)
            .set(data, SetOptions.merge())
            .await()

        Result.Success(userInfo)

    }

}