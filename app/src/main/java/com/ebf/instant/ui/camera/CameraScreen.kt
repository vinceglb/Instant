package com.ebf.instant.ui.camera

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import org.koin.androidx.compose.getViewModel

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraScreenViewModel = getViewModel(),
    onPostUploaded: () -> Unit = {},
) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    if (imageUri != EMPTY_IMAGE_URI) {
        Box(modifier = modifier) {
            AsyncImage(
                model = imageUri,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Captured image"
            )
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {

                var progressState by remember { mutableStateOf(0f) }
                val progress: Float by animateFloatAsState(targetValue = progressState)

                LinearProgressIndicator(progress = progress)

                Button(onClick = {
                    viewModel.createPost(
                        imageUri = imageUri,
                        setProgress = { progressState = it },
                        onPostUploaded = onPostUploaded
                    )
                }) {
                    Text(text = "Upload 🚀")
                }
                Button(onClick = { imageUri = EMPTY_IMAGE_URI }) {
                    Text("Remove image")
                }
            }

        }
    } else {
        var showGallerySelect by remember { mutableStateOf(false) }
        if (showGallerySelect) {
            GallerySelect(
                modifier = modifier,
                onImageUri = { uri ->
                    showGallerySelect = false
                    imageUri = uri
                }
            )
        } else {
            Box(modifier = modifier) {
                CameraCapture(
                    modifier = modifier,
                    onImageFile = { file ->
                        imageUri = file.toUri()
                    }
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(4.dp),
                    onClick = {
                        showGallerySelect = true
                    }
                ) {
                    Text("Select from Gallery")
                }
            }
        }
    }
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")
