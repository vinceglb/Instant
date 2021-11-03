package com.ebf.instant.ui

import androidx.lifecycle.ViewModel
import com.ebf.instant.ui.signin.SignInViewModelDelegate

class InstantAppViewModel(
    signInViewModelDelegate: SignInViewModelDelegate
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate
