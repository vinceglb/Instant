package com.ebf.instant.ui.signin

import com.ebf.instant.data.network.auth.AuthenticatedUserInfo
import com.ebf.instant.data.repository.AuthRepository
import com.ebf.instant.model.User
import com.ebf.instant.util.WhileViewSubscribed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

interface SignInViewModelDelegate {

    /**
     * Live updated value of the current firebase user
     */
    val userInfo: StateFlow<AuthenticatedUserInfo?>

    val userId: StateFlow<String?>

    /**
     * Returns the current user ID or null if not available.
     */
    val userIdValue: String?

    val userValue: User?

    val userState: StateFlow<Ok>

}

/**
 * Implementation of SignInViewModelDelegate that uses Firebase's auth mechanisms.
 */
internal class FirebaseSignInViewModelDelegate(
    authRepository: AuthRepository,
    applicationScope: CoroutineScope
) : SignInViewModelDelegate {

    private val currentFirebaseUser: Flow<Result<AuthenticatedUserInfo>> =
        authRepository.authStateChanges().map {
            Timber.d(it.toString())
            if (it.isFailure) {
                Timber.e(it.exceptionOrNull())
            }
            it
        }

    override val userInfo: StateFlow<AuthenticatedUserInfo?> =
        currentFirebaseUser
            .map { Timber.d(it.getOrNull()?.getState()?.toString()); it.getOrNull() }
            .stateIn(applicationScope, WhileViewSubscribed, null)

    override val userState: StateFlow<Ok> =
        userInfo
            .map { it?.getState() ?: Ok.LOADING }
            .stateIn(applicationScope, SharingStarted.Eagerly, Ok.LOADING)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val userId: StateFlow<String?> =
        userInfo
            .mapLatest { it?.getUid() }
            .stateIn(applicationScope, WhileViewSubscribed, null)

    override val userIdValue: String?
        get() = userInfo.value?.getUid()

    override val userValue: User?
        get() = userInfo.value?.let {
            User(
                id = it.getUid()!!,
                name = it.getName()!!,
                username = it.getUsername()!!,
                imageUrl = it.getProfilePictureUrl()!!
            )
        }
}

enum class Ok {
    LOADING,
    LOGGED_NOT_VALID,
    LOGGED_VALID,
    NOT_CONNECTED
}
