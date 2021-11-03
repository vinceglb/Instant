package com.ebf.instant.data.signin.datasources

import com.ebf.instant.data.signin.AuthenticatedUserInfoBasic
import com.ebf.instant.data.signin.FirebaseUserInfo
import com.ebf.instant.fcm.FcmTokenUpdater
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber

class FirebaseAuthStateUserDataSource(
    externalScope: CoroutineScope,
    private val firebase: FirebaseAuth,
    private val tokenUpdater: FcmTokenUpdater,
) : AuthStateUserDataSource {

    /**
     * The `shareIn` operator lets us reuse the flow when multiple subscribers collect at the same
     * time. With the `SharingStarted.WhileSubscribed()` policy, the flow starts when the first
     * subscriber appears and stops when there are no subscribers. This optimizes the implementation
     * of the flow since we'll have **only one** authStateListener added to Firebase when there is
     * at least one subscriber, and no listeners in Firebase when there are no subscribers.
     */
    private val basicUserInfo: SharedFlow<Result<AuthenticatedUserInfoBasic>> =
        callbackFlow {
            val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
                // This callback gets always executed on the main thread because of Firebase
                Timber.d(auth.currentUser?.uid.toString())
                trySend(auth)
            }
            firebase.addAuthStateListener(authStateListener)
            awaitClose { firebase.removeAuthStateListener(authStateListener) }
        }.map { authState ->
            // This map gets executed in the Flow's context
            processAuthState(authState)
        }.shareIn(
            scope = externalScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )

    override fun getBasicUserInfo(): Flow<Result<AuthenticatedUserInfoBasic>> = basicUserInfo

    private fun processAuthState(auth: FirebaseAuth): Result<AuthenticatedUserInfoBasic> {
        // Listener that saves the [FirebaseUser], fetches the ID token
        // and updates the user ID observable.
        Timber.d("Received a FirebaseAuth update.")

        auth.currentUser?.let { currentUser ->
            // Save the FCM ID token in firestore
            tokenUpdater.updateTokenForUser(currentUser.uid)
        }

        return Result.success(FirebaseUserInfo(auth.currentUser))
    }

}
