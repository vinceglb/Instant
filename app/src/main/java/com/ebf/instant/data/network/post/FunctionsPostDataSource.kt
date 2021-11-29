package com.ebf.instant.data.network.post

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FunctionsPostDataSource(
    private val functions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun likePost(postId: String): Unit = withContext(ioDispatcher) {
        functions
            .getHttpsCallable("like")
            .call(postId)
            .await()
    }

    suspend fun removeLikePost(postId: String): Unit = withContext(ioDispatcher) {
        functions
            .getHttpsCallable("removeLike")
            .call(postId)
            .await()
    }

    suspend fun createPost(imageUrl: String): Unit = withContext(ioDispatcher) {
        functions
            .getHttpsCallable("publishPost")
            .call(imageUrl)
            .await()
    }

}
