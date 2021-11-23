package com.ebf.instant.data2.network.user

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FunctionsUserDataSource(
    private val functions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun createAccount(name: String, username: String, imageUrl: String) =
        withContext(ioDispatcher) {
            val data = mapOf(
                "name" to name,
                "username" to username,
                "imageUrl" to imageUrl
            )

            functions
                .getHttpsCallable("createAccount")
                .call(data)
                .await()
        }
}
