package com.ebf.instant.ui.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.data.repository.UserRepository
import com.ebf.instant.ui.camera.EMPTY_IMAGE_URI
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateAccountViewModel(
    signInViewModelDelegate: SignInViewModelDelegate,
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    private val _name = MutableStateFlow("")
    private val _username = MutableStateFlow("")
    private val _imageUri = MutableStateFlow(EMPTY_IMAGE_URI)
    private val _progress = MutableStateFlow(0f)

    private val _state = MutableStateFlow(CreateAccountViewState())
    val state: StateFlow<CreateAccountViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(_name, _username, _imageUri, _progress) { name, username, imageUri, progress ->
                CreateAccountViewState(
                    name = name,
                    username = username,
                    imageUri = imageUri,
                    progress = progress,
                    inProgress = progress > 0
                )
            }.collect { _state.value = it }
        }
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setImageUri(imageUri: Uri) {
        _imageUri.value = imageUri
    }

    fun createAccount() {
        // If the user info are not valid
        if (!_state.value.enableCreateButton) {
            Timber.w("The user info are not valid: ${_state.value}")
            return
        }

        externalScope.launch {
            userIdValue?.let { uid ->
                userRepository.createAccount(
                    currentUserId = uid,
                    name = state.value.name,
                    username = state.value.username,
                    imageUri = state.value.imageUri,
                    setProgress = { _progress.value = it }
                )
            }
        }
    }

}

data class CreateAccountViewState(
    val name: String = "",
    val username: String = "",
    val imageUri: Uri = EMPTY_IMAGE_URI,
    val progress: Float = 0f,
    val inProgress: Boolean = false
) {

    val enableCreateButton: Boolean =
        name.isNotBlank() && username.isNotBlank() && imageUri != EMPTY_IMAGE_URI && !inProgress

}
