package com.ebf.instant.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

@Entity
data class Post(
    @PrimaryKey
    val id: String,
    val imageUrl: String,
    val date: Date,
    val userId: String,
    // val comments: List<String>
)

data class PostWithUser(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User
)

data class PostToPublish(
    val imageUrl: String,
    val user: User,
)
