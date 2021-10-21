package com.ebf.instant.repo

import android.net.Uri
import com.ebf.instant.local.dao.CommentDao
import com.ebf.instant.local.dao.LikeDao
import com.ebf.instant.local.dao.PostDao
import com.ebf.instant.local.dao.UserDao
import com.ebf.instant.model.*
import com.ebf.instant.remote.PostDataSource
import com.ebf.instant.remote.StorageDataSource
import com.ebf.instant.remote.StorageDataSource.StorageUploadState
import com.ebf.instant.remote.UserDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*

class PostRepository(
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val storageDataSource: StorageDataSource,
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val likeDao: LikeDao,
    private val auth: FirebaseAuth
) {

    fun getAllPosts(): Flow<List<PostWithData>> = flow {
        // First, get posts from cache
        val listFromCache = postDao.loadAll().first()
        emit(listFromCache)

        // Second, fetch posts from internet
        val listFromNetwork = postDataSource.getAllPosts()

        // Third, get new fresh data into variable
        var users = mutableListOf<User>()
        val posts = mutableListOf<Post>()
        val comments = mutableListOf<Comment>()
        val likes = mutableListOf<Like>()
        listFromNetwork.forEach { post ->
            users.add(post.user)
            posts.add(post.post)

            post.comments.forEach { comment ->
                users.add(comment.user)
                comments.add(comment.comment)
            }

            post.likes.forEach {
                users.add(it.user)
                likes.add(it.like)
            }
        }

        // We distinct because we have probably the same user multiple times
        users = users.distinct().toMutableList()

        // Save all data in room
        userDao.insertList(users)
        postDao.insertList(posts)
        commentDao.insertList(comments)
        likeDao.insertList(likes)

        // Finally, observe the database, the unique source of true
        emitAll(postDao.loadAll())
    }

    fun getAllCommentsFromPost(postId: String): Flow<List<CommentWithUser>> {
        return commentDao.commentsFromPost(postId = postId)
    }

    suspend fun publishPost(urlImage: String) {
        // Get the user info
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("The user must be log in")
        val user = userDataSource.getUserById(uid)

        //  Publish the post
        val postToPublish = PostToPublish(
            imageUrl = urlImage,
            user = user
        )
        postDataSource.publishPost(postToPublish = postToPublish)
    }

    suspend fun publishComment(postId: String, message: String) {
        // Get the user info
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("The user must be log in")
        val user = userDataSource.getUserById(uid)

        // Get temp id
        val tempId = UUID.randomUUID().toString()
        val commentWithUser = CommentWithUser(
            comment = Comment(
                id = tempId,
                date = Date(),
                content = message,
                userId = user.id,
                postId = postId
            ),
            user = user
        )

        // Save temp comment
        commentDao.insert(user = user, comment = commentWithUser.comment)

        // Publish comment
        val commentId = postDataSource.commentPost(commentWithUser = commentWithUser)

        // Save the new published comment in local db
        commentDao.removeComment(comment = commentWithUser.comment)
        commentDao.insert(user = user, comment = commentWithUser.comment.copy(id = commentId))
    }

    suspend fun likeOrDislikePost(postId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("The user must be log in")

        // Get the like if the user already liked the post
        val likeFromDb = likeDao.getLike(userId = uid, postId = postId)

        if (likeFromDb == null) {
            val user = userDataSource.getUserById(uid)
            val likeWithUser = LikeWithUser(
                like = Like(
                    id = "$postId-$uid",
                    date = Date(),
                    userId = user.id,
                    postId = postId
                ),
                user = user
            )
            likeDao.insert(user = user, like = likeWithUser.like)
            postDataSource.likePost(likeWithUser = likeWithUser)
        } else {
            // Remove from local DB
            likeDao.remove(likeFromDb)

            // Remove from network
            postDataSource.dislikePost(postId = postId, userId = uid)
        }
    }

    fun uploadImage(imageUri: Uri): StorageUploadState =
        storageDataSource.uploadImage(imageUri)

}
