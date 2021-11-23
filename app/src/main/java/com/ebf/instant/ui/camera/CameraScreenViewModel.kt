package com.ebf.instant.ui.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.data2.repository.PostRepository
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import kotlinx.coroutines.launch

class CameraScreenViewModel(
    delegate: SignInViewModelDelegate,
    private val postRepository: PostRepository,
) : ViewModel(), SignInViewModelDelegate by delegate {

    fun createPost(imageUri: Uri, setProgress: (Float) -> Unit) = viewModelScope.launch {
        userIdValue?.let {
            postRepository.createPost(currentUserId = it, imageUri = imageUri, setProgress = setProgress)
        }
    }

}
