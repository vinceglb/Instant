package com.ebf.instant.data.repository

import com.ebf.instant.data.database.dao.CommentDao
import com.ebf.instant.data.network.comment.FunctionsCommentDataSource
import com.ebf.instant.model.Comment
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

class CommentRepository(
    private val commentDataSource: FunctionsCommentDataSource,
    private val commentDao: CommentDao
) {

    fun getAllCommentsFromPost(postId: String): Flow<List<CommentWithUser>> =
        commentDao.commentsFromPost(postId = postId)

    suspend fun publishComment(currentUser: User, postId: String, message: String) {
        // Get temp id
        val tempId = UUID.randomUUID().toString()
        val comment = Comment(
            id = tempId,
            createDate = Date(),
            content = message,
            userId = currentUser.id,
            postId = postId
        )

        // Save temp comment
        commentDao.insert(user = currentUser, comment = comment)

        // Publish comment
        val commentId = commentDataSource.publishComment(postId = postId, content = message)

        // Save the new published comment in local db
        commentDao.removeComment(comment = comment)
        commentDao.insert(user = currentUser, comment = comment.copy(id = commentId))
    }

}