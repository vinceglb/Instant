package com.ebf.instant.data.user

import com.ebf.instant.Result
import com.ebf.instant.data.signin.UserInfo

class UserRepository(
    private val firestoreUserDataSource: FirestoreUserDataSource
) {

    suspend fun updateUserInfo(
        userId: String,
        userInfo: UserInfo
    ): Result<UserInfo> = firestoreUserDataSource.updateUserInfo(userId, userInfo)

}
