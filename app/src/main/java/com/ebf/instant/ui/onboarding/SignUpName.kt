package com.ebf.instant.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebf.instant.ui.theme.InstantTheme


//@Composable
//fun Forms(user: InstantUser, onNewData: (InstantUser) -> Unit) {
//
//    if (user.name == null) {
//        SignUpForm(
//            title = "What is your name ?",
//            placeholder = "John Doe",
//            onButtonClicked = {}
//        )
//    } else if (user.username == null) {
//        SignUpForm(
//            title = "What is your username ?",
//            placeholder = "john.doe",
//            subtitle = "It is unique and will help your friends find you easily.",
//            onButtonClicked = { username ->
//                val newUser = user.copy(username = username)
//                onNewData(newUser)
//            }
//        )
//    }
//
//}

@Composable
fun SignUpForm(
    title: String,
    placeholder: String,
    onButtonClicked: (String) -> Unit,
    subtitle: String? = null,
) {
    // It's the text enter by the user in the text field
    var data by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(all = 40.dp)
    ) {

        // The title
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
        )

        // The subtitle
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = subtitle,
                textAlign = TextAlign.Center
            )
        }

        // The text field
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedTextField(
            value = data,
            onValueChange = { data = it },
            placeholder = { Text(text = placeholder) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(80.dp))

        // The next button
        Button(
            onClick = { onButtonClicked(data.trim()) },
            enabled = data.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Continue")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SignUpFormPreview() {
    InstantTheme {
        SignUpForm(
            title = "What is your name ?",
            placeholder = "Johan Elias",
            onButtonClicked = {},
            subtitle = "Cela permetra à tes amis de facilement te retrouver."
        )
    }
}

