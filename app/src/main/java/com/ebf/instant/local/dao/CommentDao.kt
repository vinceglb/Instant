package com.ebf.instant.local.dao

import androidx.room.*
import com.ebf.instant.model.Comment
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User, comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(comments: List<Comment>)

    @Transaction
    @Query("SELECT * FROM comment WHERE postId = :postId ORDER BY date DESC")
    fun commentsFromPost(postId: String): Flow<List<CommentWithUser>>

    @Delete
    suspend fun removeComment(comment: Comment)

}
