package com.ebf.instant.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class StorageDataSource(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
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

    data class StorageUploadState(
        val task: UploadTask,
        val reference: StorageReference
    )

}
