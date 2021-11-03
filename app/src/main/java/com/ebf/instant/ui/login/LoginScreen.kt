package com.ebf.instant.ui.login

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebf.instant.R
import com.ebf.instant.ui.theme.InstantTheme
import com.ebf.instant.util.signin.LoginWithGoogle
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginScreen(vm: LoginScreenViewModel = getViewModel()) {
    val googleLoginLauncher = rememberLauncherForActivityResult(LoginWithGoogle()) { token ->
        token?.let { vm.connectWithGoogle(it) }
    }

    LoginScreenContent(loginWithGoogle = { googleLoginLauncher.launch() })
}

@Composable
fun LoginScreenContent(loginWithGoogle: () -> Unit) {
    Scaffold(
        bottomBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = 40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.doodles_together),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Share your Instant",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(id = R.string.welcome_subtitle),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(80.dp))

                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)

                TextButton(
                    onClick = { loginWithGoogle() },
                    modifier = buttonModifier
                ) {
                    Text(text = "I already have an account")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { loginWithGoogle() },
                    modifier = buttonModifier
                ) {
                    Text(text = "Let's go !")
                }

            }
        },
        content = {}
    )
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    InstantTheme {
        LoginScreenContent { }
    }
}
