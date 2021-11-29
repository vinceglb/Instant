package com.ebf.instant.data.network.user

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FunctionsUserDataSource(
    private val functions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun createAccount(name: String, username: String, imageUrl: String): Result<Unit> =
        withContext(ioDispatcher) {
            val data = mapOf(
                USER_NAME to name,
                USER_USERNAME to username,
                USER_IMAGE_URL to imageUrl
            )

            functions
                .getHttpsCallable("createAccount")
                .call(data)
                .await()

            Result.success(Unit)
        }

    companion object {
        const val USER_ID = "id"
        const val USER_USERNAME = "username"
        const val USER_NAME = "name"
        const val USER_IMAGE_URL = "imageUrl"
    }

}
