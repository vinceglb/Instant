package com.ebf.instant.local.dao

import androidx.room.*
import com.ebf.instant.model.PostWithUser
import com.ebf.instant.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(posts: List<Post>)

    @Transaction
    @Query("SELECT * FROM Post")
    fun loadAll(): Flow<List<PostWithUser>>

}
