package com.ebf.instant.ui.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.remote.StorageDataSource.StorageUploadState
import com.ebf.instant.repo.PostRepository
import kotlinx.coroutines.launch

class CameraScreenViewModel(private val postRepository: PostRepository) : ViewModel() {

    fun uploadImage(imageUri: Uri): StorageUploadState = postRepository.uploadImage(imageUri)

    fun publishPost(imageUrl: String, onPublished: () -> Unit) {
        viewModelScope.launch {
            postRepository.publishPost(imageUrl)
            onPublished()
        }
    }

}