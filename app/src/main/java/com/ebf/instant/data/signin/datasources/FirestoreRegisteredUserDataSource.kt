package com.ebf.instant.data.signin.datasources

import com.ebf.instant.data.signin.UserInfo
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber

/**
 * A [RegisteredUserDataSource] that listens to changes in firestore to indicate whether the
 * current user is registered in the event or not as an attendee.
 */
class FirestoreRegisteredUserDataSource (
    private val firestore: FirebaseFirestore
) : RegisteredUserDataSource {

    override fun observeUserChanges(userId: String): Flow<Result<UserInfo>> {
        return callbackFlow {
            // Watch the document
            val registeredChangedListener =
                { snapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                    if (snapshot == null || !snapshot.exists()) {
                        // When the account signs in for the first time, the document doesn't exist
                        Timber.d("Document for snapshot $userId doesn't exist")
                        trySend(Result.success(UserInfo(null, null, null)))
                    } else {
                        val userInfo = UserInfo(
                            name = snapshot.getString(NAME_KEY),
                            username = snapshot.getString(USERNAME_KEY),
                            imageUrl = snapshot.getString(IMAGE_URL_KEY)
                        )
                        Timber.d("Received user info: $userInfo")
                        trySend(Result.success(userInfo))
                    }
                    Unit // Avoids returning the Boolean from channel.offer
                }

            val registeredChangedListenerSubscription = firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener(registeredChangedListener)

            awaitClose { registeredChangedListenerSubscription.remove() }
        }
            // Only emit a value if it's a new value or a value change.
            .distinctUntilChanged()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val NAME_KEY = "name"
        private const val USERNAME_KEY = "username"
        private const val IMAGE_URL_KEY = "imageUrl"
    }

}
