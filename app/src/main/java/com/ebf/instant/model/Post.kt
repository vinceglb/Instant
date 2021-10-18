package com.ebf.instant.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Post(
    @PrimaryKey
    val id: String,
    val imageUrl: String,
    val date: Date,
    @Embedded(prefix = "user_") val user: User,
)

data class PostToPublish(
    val imageUrl: String,
    val user: User,
)
