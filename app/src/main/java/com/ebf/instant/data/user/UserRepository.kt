package com.ebf.instant.data.user

import android.net.Uri
import com.ebf.instant.data.post.StoragePostDataSource

class UserRepository(
    private val functionsUserDataSource: FunctionsUserDataSource,
    private val storagePostDataSource: StoragePostDataSource,
) {

    suspend fun createAccount(
        currentUserId: String,
        name: String,
        username: String,
        imageUri: Uri,
        setProgress: (Float) -> Unit
    ) {
        val url = storagePostDataSource.uploadImage(currentUserId, imageUri, setProgress)
        functionsUserDataSource.createAccount(name, username, url)
    }

}
