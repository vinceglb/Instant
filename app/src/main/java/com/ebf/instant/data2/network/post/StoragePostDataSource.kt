package com.ebf.instant.data2.network.post

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class StoragePostDataSource(
    private val storage: FirebaseStorage,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun uploadImage(
        currentUserId: String,
        imageUri: Uri,
        setProgress: (Float) -> Unit
    ): String = withContext(externalScope.coroutineContext + ioDispatcher) {
        // Get image extension
        val clip = imageUri.lastPathSegment?.split(".") ?: emptyList()
        val extension = clip.getOrElse(1) { "jpg" }

        // Random name file
        val uuid = UUID.randomUUID()

        // Create image ref in storage
        val imageRef = storage.reference.child("user/$currentUserId/$uuid.$extension")

        // Upload image
        val task = imageRef.putFile(imageUri)
        task.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            setProgress((bytesTransferred * 1f) / totalByteCount)
        }.await()

        // Get download url
        imageRef.downloadUrl.await().toString()
    }

}