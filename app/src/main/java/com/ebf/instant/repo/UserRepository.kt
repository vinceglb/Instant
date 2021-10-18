package com.ebf.instant.repo

import com.ebf.instant.model.User
import com.ebf.instant.model.UserPartial
import com.ebf.instant.remote.UserDataSource

class UserRepository(private val userDataSource: UserDataSource) {

    suspend fun getUserById(userId: String): User =
        userDataSource.getUserById(userId = userId)

    suspend fun updateMyUserInfo(userPartial: UserPartial): User =
        userDataSource.updateUserInfo(userPartial = userPartial)

}
