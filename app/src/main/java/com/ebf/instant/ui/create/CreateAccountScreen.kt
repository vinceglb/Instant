package com.ebf.instant.ui.create

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.getViewModel

@Composable
fun CreateAccount(vm: CreateAccountViewModel = getViewModel()) {
    Column {
        Text(text = "Create Account")
        Button(onClick = { vm.updateUserInfo() }) {
            Text(text = "Create now")
        }
    }
}
