package com.ebf.instant.repo

import android.net.Uri
import com.ebf.instant.local.dao.PostDao
import com.ebf.instant.local.dao.UserDao
import com.ebf.instant.model.Post
import com.ebf.instant.model.PostToPublish
import com.ebf.instant.remote.PostDataSource
import com.ebf.instant.remote.StorageDataSource
import com.ebf.instant.remote.StorageDataSource.StorageUploadState
import com.ebf.instant.remote.UserDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class PostRepository(
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val storageDataSource: StorageDataSource,
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val auth: FirebaseAuth
) {

    fun getAllPosts(): Flow<List<Post>> = flow {
        // First, get posts from cache
        val listFromCache = postDao.loadAll().first()
        emit(listFromCache)

        // Second, fetch posts from internet
        val listFromNetwork = postDataSource.getAllPosts()
        listFromNetwork.forEach {
            userDao.insert(it.user)
            postDao.insert(it.post)
        }

        // Third, observe the database, the unique source of true
        emitAll(postDao.loadAll())
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

    fun uploadImage(imageUri: Uri): StorageUploadState =
        storageDataSource.uploadImage(imageUri)

}
