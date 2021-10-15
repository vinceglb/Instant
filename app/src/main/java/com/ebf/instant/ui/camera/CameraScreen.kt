package com.ebf.instant.ui.camera

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage

@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    if (imageUri != EMPTY_IMAGE_URI) {
        Box(modifier = modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {

                var progress by remember { mutableStateOf(0f) }

                LinearProgressIndicator(progress = progress)

                Button(onClick = {

                    val storage = Firebase.storage
                    val storageRef = storage.reference
                    val vinceImage = storageRef.child("user/${Firebase.auth.currentUser!!.uid}/${imageUri.lastPathSegment}")
                    val uploadTask = vinceImage.putFile(imageUri)

                    uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
                        progress = (bytesTransferred * 1f) / totalByteCount
                        Log.d("Vince", "$progress")
                    }

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
