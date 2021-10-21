package com.ebf.instant.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

@Entity
data class Comment(
    @PrimaryKey
    val id: String,
    val date: Date,
    val content: String,
    val userId: String,
    val postId: String
)

data class CommentWithUser(
    @Embedded val comment: Comment,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User
)
