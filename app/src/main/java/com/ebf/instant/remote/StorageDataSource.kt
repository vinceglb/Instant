package com.ebf.instant.remote

import android.content.Context
import android.net.Uri
import com.ebf.instant.util.Result
import com.ebf.instant.util.Result.Error
import com.ebf.instant.util.Result.Success
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import kotlinx.coroutines.tasks.await

class StorageDataSource(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val context: Context
) {

    fun uploadImage(imageUri: Uri): StorageUploadState {
        // The user must not be null to proceed
        val user = auth.currentUser ?: throw IllegalArgumentException("Auth user must not be null")

        val clip = imageUri.lastPathSegment?.split(".") ?: emptyList()
        val fileName = clip.getOrElse(0) { "image" }
        val extension = clip.getOrElse(1) { "jpg" }

        val imageRef = storage.reference.child("user/${user.uid}/$fileName.$extension")
        return StorageUploadState(
            task = imageRef.putFile(imageUri),
            reference = imageRef
        )
    }

    /**
     * TODO catch errors
     */
    suspend fun uploadImageFullProcess(
        imageUri: Uri,
        onProgress: (Float) -> Unit,
    ): Result<Uri> {
        // TODO Compress image
        // val compressedImageFile = Compressor.compress(context = context, imageFile = imageUri.toFile())

        // Prepare the upload to Firebase Storage
        val uploadState = uploadImage(imageUri = imageUri)

        return try {
            // Upload image
            uploadState.task.addOnProgressListener { (bytesTransferred, totalByteCount) ->
                // Compute the progress
                val progress = (bytesTransferred * 1f) / totalByteCount
                onProgress(progress)
            }.await()

            // Get the download url
            val downloadUri = uploadState.reference.downloadUrl.await()
            Success(downloadUri)
        } catch (e: Exception) {
            Error(e)
        }

        // Get image url
//        uploadState.task.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            uploadState.reference.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // The image url
//                val downloadUri = task.result
//                Timber.d("Ok here $downloadUri")
//                onSuccess(downloadUri)
//            } else {
//                onError(task.exception!!)
//            }
//        }
    }

    data class StorageUploadState(
        val task: UploadTask,
        val reference: StorageReference
    )

}
