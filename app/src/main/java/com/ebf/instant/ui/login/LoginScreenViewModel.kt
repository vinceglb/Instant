package com.ebf.instant.ui.login

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.model.User
import com.ebf.instant.model.UserPartial
import com.ebf.instant.remote.StorageDataSource
import com.ebf.instant.repo.UserRepository
import com.ebf.instant.util.Result
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * UI State for the Login Screen
 */
data class SignUpUiState(
    val loading: Boolean,
    val user: User,
    val progress: Float
)

/**
 * ViewModel that handles the business logic of the Login screen
 */
class LoginScreenViewModel(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val storageService: StorageDataSource
) : ViewModel() {

    val loadingState = MutableStateFlow(LoadingState.IDLE)

//    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
//        try {
//            loadingState.emit(LoadingState.LOADING)
//            auth.signInWithEmailAndPassword(email, password).await()
//            loadingState.emit(LoadingState.LOADED)
//        } catch (e: Exception) {
//            loadingState.emit(LoadingState.error(e.localizedMessage))
//        }
//    }

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            auth.signInWithCredential(credential).await()
            getUserInfo()
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    // UI state exposed to the UI
    private val _uiState =
        MutableStateFlow(SignUpUiState(loading = false, user = User("", "", "", ""), progress = 0f))
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private fun getUserInfo() = viewModelScope.launch {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val user = userRepository.getUserById(firebaseUser.uid)
            _uiState.update {
                it.copy(user = user)
            }
        } else {
            Timber.w("The firebase user is not log in.")
        }

        // Stop Ui state refreshing
        _uiState.update { it.copy(loading = false) }
    }

    init {
        getUserInfo()
    }

    fun updateUserInfo(
        username: String? = null,
        name: String? = null,
        imageUrl: String? = null
    ) = viewModelScope.launch {
        auth.currentUser?.uid?.let { uid ->
            // Ui state is refreshing
            _uiState.update { it.copy(loading = true) }

            // Update user info
            val newUser = userRepository.updateMyUserInfo(
                userPartial = UserPartial(
                    id = uid,
                    username = username orIfEmpty uiState.value.user.username,
                    name = name orIfEmpty uiState.value.user.name,
                    imageUrl = imageUrl orIfEmpty uiState.value.user.imageUrl,
                )
            )

            // Update the state with new fresh data
            _uiState.update {
                it.copy(
                    loading = false,
                    user = newUser
                )
            }
        } ?: Timber.w("The firebase user is not login.")
    }

    fun uploadProfilePictureAndUpdateUserInfo(imageUri: Uri) = viewModelScope.launch {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        Timber.d("0")

        // Upload image
        val result = storageService.uploadImageFullProcess(
            imageUri = imageUri,
            onProgress = { progress ->
                _uiState.update { it.copy(progress = progress) }
                Timber.d("Progress ${progress * 100}%")
            },
        )

        when (result) {
            is Result.Success -> updateUserInfo(imageUrl = result.data.toString())
            is Result.Error -> {
                /* TODO display error */
                Timber.e(result.exception)
            }
        }
    }

}

private infix fun String?.orIfEmpty(other: String): String =
    if (this?.isNotEmpty() == true) this else other
