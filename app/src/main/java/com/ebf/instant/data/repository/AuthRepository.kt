package com.ebf.instant.data.repository

import com.ebf.instant.data.network.auth.AuthenticatedUserInfo
import com.ebf.instant.data.network.auth.ObserveUserAuthStateUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val observeUserAuthStateUseCase: ObserveUserAuthStateUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun loginWithGoogle(idToken: String) {
        withContext(ioDispatcher) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
        }
    }

    fun authStateChanges(): Flow<Result<AuthenticatedUserInfo>> =
        observeUserAuthStateUseCase()

}
