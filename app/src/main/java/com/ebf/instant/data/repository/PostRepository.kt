package com.ebf.instant.data.repository

import android.net.Uri
import com.ebf.instant.data.database.dao.CommentDao
import com.ebf.instant.data.database.dao.LikeDao
import com.ebf.instant.data.database.dao.PostDao
import com.ebf.instant.data.database.dao.UserDao
import com.ebf.instant.data.network.post.FirestorePostDataSource
import com.ebf.instant.data.network.post.FunctionsPostDataSource
import com.ebf.instant.data.network.post.StoragePostDataSource
import com.ebf.instant.model.Comment
import com.ebf.instant.model.Like
import com.ebf.instant.model.LikeWithUser
import com.ebf.instant.model.Post
import com.ebf.instant.model.PostWithData
import com.ebf.instant.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

class PostRepository(
    private val postDataSource: FirestorePostDataSource,
    private val functionsPostDataSource: FunctionsPostDataSource,
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val likeDao: LikeDao,
    private val commentDao: CommentDao,
    private val storagePostDataSource: StoragePostDataSource,
    private val appDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    fun getAllPosts(): Flow<List<PostWithData>> = flow {
        // First, get posts from cache
        val listFromCache = postDao.loadAll().first()
        emit(listFromCache)

        // Second, fetch posts from internet
        val listFromNetwork = postDataSource.fetchAllPosts()

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
    }.flowOn(appDispatcher)

    suspend fun likeOrDislikePost(currentUser: User, postId: String) {

        // Get the like if the user already liked the post
        @Suppress("MoveVariableDeclarationIntoWhen")
        val likeFromDb = likeDao.getLike(userId = currentUser.id, postId = postId)

        when (likeFromDb) {

            null -> {
                val likeWithUser = LikeWithUser(
                    like = Like(
                        id = "$postId-${currentUser.id}",
                        date = Date(),
                        userId = currentUser.id,
                        postId = postId
                    ),
                    user = currentUser
                )
                likeDao.insert(user = currentUser, like = likeWithUser.like)
                functionsPostDataSource.likePost(postId = postId)
            }

            else -> {
                // Remove from local DB if exists
                likeDao.remove(likeFromDb)

                // Remove from network
                functionsPostDataSource.removeLikePost(postId = postId)
            }

        }
    }

    suspend fun createPost(
        currentUserId: String,
        imageUri: Uri,
        onPostUploaded: () -> Unit,
        setProgress: (Float) -> Unit
    ) {
        val url = storagePostDataSource.uploadImage(currentUserId, imageUri, setProgress)
        functionsPostDataSource.createPost(url)
        onPostUploaded()
    }
}
