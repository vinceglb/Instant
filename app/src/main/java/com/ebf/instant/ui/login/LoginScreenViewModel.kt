package com.ebf.instant.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun connectWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken)
        }
    }

}
