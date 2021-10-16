package com.ebf.instant.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
data class Post(
    @PrimaryKey
    val id: String,
    val imageUrl: String,
    val date: ZonedDateTime,
    @Embedded(prefix = "user_") val user: User,
)
