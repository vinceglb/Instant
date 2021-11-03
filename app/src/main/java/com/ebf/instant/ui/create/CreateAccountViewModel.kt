package com.ebf.instant.ui.create

import androidx.lifecycle.ViewModel
import com.ebf.instant.data.signin.UserInfo
import com.ebf.instant.data.user.UserRepository
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CreateAccountViewModel(
    signInViewModelDelegate: SignInViewModelDelegate,
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    fun updateUserInfo() {
        externalScope.launch {
            userIdValue?.let { uid ->
                val userInfo = UserInfo(
                    name = "Vince",
                    username = "vince.app",
                    imageUrl = "url"
                )

                userRepository.updateUserInfo(
                    userInfo = userInfo,
                    userId = uid
                )
            }
        }
    }

}