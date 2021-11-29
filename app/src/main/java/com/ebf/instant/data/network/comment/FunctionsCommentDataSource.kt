package com.ebf.instant.data.network.comment

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FunctionsCommentDataSource(
    private val functions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun publishComment(postId: String, content: String): String =
        withContext(ioDispatcher) {
            val data = mapOf(
                "postId" to postId,
                "content" to content
            )

            val res = functions
                .getHttpsCallable("comment")
                .call(data)
                .await()

            res.data as String
        }

}