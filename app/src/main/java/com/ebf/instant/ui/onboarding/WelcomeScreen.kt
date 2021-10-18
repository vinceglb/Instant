package com.ebf.instant.ui.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ebf.instant.R
import com.ebf.instant.ui.AppContainer
import com.ebf.instant.ui.camera.GallerySelect
import com.ebf.instant.ui.login.LoginScreenViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
fun WelcomeScreen(viewModel: LoginScreenViewModel = getViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.user.isValid) {
        Scaffold(
            bottomBar = {

                when {
                    uiState.user.id.isEmpty() -> {
                        WelcomeContent(viewModel = viewModel)
                    }
                    uiState.user.name.isEmpty() -> {
                        SignUpForm(
                            title = "What is your name ?",
                            placeholder = "John Doe",
                            onButtonClicked = { viewModel.updateUserInfo(name = it) }
                        )
                    }
                    uiState.user.username.isEmpty() -> {
                        SignUpForm(
                            title = "What is your username ?",
                            placeholder = "john.doe",
                            subtitle = "Your username is unique and will help your friends to find you easily.",
                            onButtonClicked = { viewModel.updateUserInfo(username = it) }
                        )
                    }
                    uiState.user.imageUrl.isEmpty() -> {
                        var buttonClicked by remember { mutableStateOf(false) }
                        var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }

                        Column {
                            Button(onClick = { buttonClicked = true }) {
                                Text(text = "Choose image")
                            }

                            if (buttonClicked) {
                                GallerySelect(onImageUri = {
                                    buttonClicked = false
                                    imageUri = it
                                })
                            }

                            if (imageUri != EMPTY_IMAGE_URI) {
                                Button(onClick = {
                                    viewModel.uploadProfilePictureAndUpdateUserInfo(imageUri = imageUri)
                                }) {
                                    Text(text = "Upload")
                                }
                            }
                        }

                    }
                }

            },
            content = {}
        )
    } else {
        AppContainer()
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeContent(
    viewModel: LoginScreenViewModel,
    onLoginSuccess: (String) -> Unit ={}
) {
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)

    // Equivalent of onActivityResult
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                viewModel.signWithCredential(credential)
                onLoginSuccess(account.id!!)
            } catch (e: ApiException) {
                Timber.w(e, "Google sign in failed")
            }
        }

    fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        launcher.launch(googleSignInClient.signInIntent)
    }

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
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")
