package com.ebf.instant.data.post

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
        val clip = imageUri.lastPathSegment?.split(".") ?: emptyList()
        val fileName = clip.getOrElse(0) { "image" }
        val extension = clip.getOrElse(1) { "jpg" }

        val imageRef = storage.reference.child("user/$currentUserId/$fileName.$extension")
        val task = imageRef.putFile(imageUri)

        task.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            setProgress((bytesTransferred * 1f) / totalByteCount)
        }

        task.await()

        imageRef.downloadUrl.await().toString()
    }

}