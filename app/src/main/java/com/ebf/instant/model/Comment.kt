package com.ebf.instant.model

import androidx.room.Embedded
import java.util.*

data class Comment(
    val id: String,
    val date: Date,
    val content: String,
    @Embedded(prefix = "user_") val user: User
)
