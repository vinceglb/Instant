package com.ebf.instant.domain.auth

import com.ebf.instant.data.signin.AuthenticatedUserInfo
import com.ebf.instant.data.signin.FirebaseRegisteredUserInfo
import com.ebf.instant.data.signin.UserInfo
import com.ebf.instant.data.signin.datasources.AuthStateUserDataSource
import com.ebf.instant.data.signin.datasources.RegisteredUserDataSource
import com.ebf.instant.util.cancelIfActive
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class ObserveUserAuthStateUseCase(
    private val authStateUserDataSource: AuthStateUserDataSource,
    private val registeredUserDataSource: RegisteredUserDataSource,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private var observeUserRegisteredChangesJob: Job? = null

    // As a separate coroutine needs to listen for user registration changes and emit to the
    // flow, a callbackFlow is used
    private val authStateChanges = callbackFlow<Result<AuthenticatedUserInfo>> {
        authStateUserDataSource.getBasicUserInfo().collect { userResult ->
            // Cancel observing previous user registered changes
            observeUserRegisteredChangesJob.cancelIfActive()

            if (userResult.isSuccess) {
                val userData = userResult.getOrThrow()

                if (userData.getUid() != null) {
                    // Observing the user registration changes from another scope to able to listen
                    // for this and updates to getBasicUserInfo() simultaneously
                    observeUserRegisteredChangesJob = externalScope.launch(ioDispatcher) {
                        // Start observing the user in Firestore to fetch the user info
                        registeredUserDataSource.observeUserChanges(userId = userData.getUid()!!)
                            .collect { result ->
                                val userInfo: UserInfo = result.getOrThrow()
                                send(
                                    Result.success(
                                        FirebaseRegisteredUserInfo(
                                            basicUserInfo = userData,
                                            username = userInfo.username,
                                            name = userInfo.name,
                                            profilePictureUrl = userInfo.imageUrl
                                        )
                                    )
                                )
                            }
                    }
                } else {
                    send(Result.success(FirebaseRegisteredUserInfo(null, null, null, null)))
                }

            } else {
                send(Result.failure(Exception("FirebaseAuth error", userResult.exceptionOrNull())))
            }
        }

        // Always wait for the flow to be closed. Specially important for tests.
        awaitClose { observeUserRegisteredChangesJob.cancelIfActive() }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed()
    )

    operator fun invoke(): Flow<Result<AuthenticatedUserInfo>> =
        (authStateChanges as Flow<Result<AuthenticatedUserInfo>>)
            .catch { e -> emit(Result.failure(e)) }
            .flowOn(ioDispatcher)

}
