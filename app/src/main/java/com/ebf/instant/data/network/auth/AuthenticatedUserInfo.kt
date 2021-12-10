package com.ebf.instant.data.network.auth

import android.net.Uri
import com.ebf.instant.ui.signin.Ok
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo

/**
 * Interface to decouple the user info from Firebase.
 *
 * @see [FirebaseRegisteredUserInfo]
 */
interface AuthenticatedUserInfo : AuthenticatedUserInfoBasic, AuthenticatedUserInfoRegistered {

    fun getState(): Ok = when {
        isValid() -> Ok.LOGGED_VALID
        isSignedIn() -> Ok.LOGGED_NOT_VALID
        else -> Ok.NOT_CONNECTED
    }

}

interface AuthenticatedUserInfoBasic2 {
    val uid: String?
    val email: String?
    val isSignedIn: Boolean
}

interface AuthenticatedUserInfoRegistered2 {
    val uid: String
    val name: String
    val username: String
    val photoUrl: String
}

class FirebaseUserInfo2(
    firebaseUser: FirebaseUser?
) : AuthenticatedUserInfoBasic2 {
    override val uid: String? = firebaseUser?.uid
    override val email: String? = firebaseUser?.email
    override val isSignedIn: Boolean = firebaseUser != null
}

/**
 * Basic user info.
 */
interface AuthenticatedUserInfoBasic {

    fun isSignedIn(): Boolean

    fun getEmail(): String?

    fun getProviderData(): MutableList<out UserInfo>?

    fun getLastSignInTimestamp(): Long?

    fun getCreationTimestamp(): Long?

    fun isAnonymous(): Boolean?

    fun getPhoneNumber(): String?

    fun getUid(): String?

    fun isEmailVerified(): Boolean?

    fun getDisplayName(): String?

    fun getPhotoUrl(): Uri?

    fun getProviderId(): String?
}

/**
 * Extra information about the auth and registration state of the user.
 */
interface AuthenticatedUserInfoRegistered {

    fun isValid(): Boolean =
        !getUsername().isNullOrBlank()
                && !getName().isNullOrBlank()
                && !getProfilePictureUrl().isNullOrBlank()

    fun getUsername(): String?

    fun getName(): String?

    fun getProfilePictureUrl(): String?

}
