package com.ebf.instant.repo

import com.ebf.instant.local.dao.PostDao
import com.ebf.instant.model.Post
import com.ebf.instant.remote.PostDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class PostRepository(
    private val dataSource: PostDataSource,
    private val postDao: PostDao
) {

    fun getAllPosts(): Flow<List<Post>> = flow {
        // First, get posts from cache
        val listFromCache = postDao.loadAll().first()
        emit(listFromCache)

        // Second, fetch posts from internet
        val listFromNetwork = dataSource.getAllPosts()
        postDao.insertList(listFromNetwork)

        // Third, observe the database, the unique source of true
        emitAll(postDao.loadAll())
    }

}
