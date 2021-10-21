package com.ebf.instant.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.ui.graphics.vector.ImageVector

enum class InstantScreen(
    val icon: ImageVector,
    val label: String
) {

    Feed(
        icon = Icons.Rounded.Home,
        label = "Home"
    ),
    Camera(
        icon = Icons.Outlined.AddBox,
        label = "Camera"
    ),
    Account(
        icon = Icons.Rounded.AccountCircle,
        label = "Account"
    ),
    Comments(
        icon = Icons.Rounded.QuestionAnswer,
        label = "Comments"
    );

    companion object {
        fun fromRoute(route: String?): InstantScreen =
            when (route?.substringBefore("/")) {
                Feed.name -> Feed
                Camera.name -> Camera
                Account.name -> Account
                Comments.name -> Comments
                null -> Feed
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}