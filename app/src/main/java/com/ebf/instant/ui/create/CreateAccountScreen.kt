package com.ebf.instant.ui.create

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ebf.instant.ui.camera.EMPTY_IMAGE_URI
import com.ebf.instant.ui.camera.GallerySelect
import com.ebf.instant.ui.theme.InstantTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun CreateAccountScreen(vm: CreateAccountViewModel = getViewModel()) {
    val viewState by vm.state.collectAsState()

    CreateAccountScreenContent(
        viewState = viewState,
        setName = { vm.setName(it) },
        setUsername = { vm.setUsername(it) },
        setImageUri = { vm.setImageUri(it) },
        onCreateAccountButtonClicked = { vm.createAccount() }
    )
}

@Composable
fun CreateAccountScreenContent(
    viewState: CreateAccountViewState,
    setName: (String) -> Unit,
    setUsername: (String) -> Unit,
    setImageUri: (Uri) -> Unit,
    onCreateAccountButtonClicked: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = 40.dp)
            ) {
                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(28.dp))

                ProfileImageChooser(
                    imageUri = viewState.imageUri,
                    setImageUri = { setImageUri(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CreateAccountTextField(
                    text = viewState.name,
                    setText = { setName(it) },
                    label = "Name",
                    placeholder = "John Doe",
                )

                Spacer(modifier = Modifier.height(12.dp))

                CreateAccountTextField(
                    text = viewState.username,
                    setText = { setUsername(it) },
                    label = "Username",
                    placeholder = "john.doe",
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onCreateAccountButtonClicked,
                    enabled = viewState.enableCreateButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Create account")
                }
            }
        },
        content = {}
    )
}

@Composable
fun CreateAccountTextField(
    text: String,
    setText: (String) -> Unit,
    label: String,
    placeholder: String,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = text,
        onValueChange = { setText(it) },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() })
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileImageChooser(imageUri: Uri, setImageUri: (Uri) -> Unit) {
    var showGallerySelect by remember { mutableStateOf(false) }

    Card(
        shape = CircleShape,
        onClick = { showGallerySelect = true },
        modifier = Modifier.size(140.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (imageUri == EMPTY_IMAGE_URI) {
                Icon(
                    Icons.Rounded.AddAPhoto,
                    contentDescription = "Profile image chooser",
                    modifier = Modifier.size(40.dp)
                )
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Account image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Show the user gallery
    if (showGallerySelect) {
        GallerySelect(onImageUri = {
            showGallerySelect = false
            setImageUri(it)
        })
    }

}

@Preview
@Composable
fun CreateAccountScreenContentPreview() {
    InstantTheme {
        CreateAccountScreenContent(
            viewState = CreateAccountViewState(),
            setImageUri = {},
            setUsername = {},
            setName = {},
            onCreateAccountButtonClicked = {}
        )
    }
}
