package com.ebf.instant.local.dao

import androidx.room.*
import com.ebf.instant.model.Post
import com.ebf.instant.model.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(posts: List<PostEntity>)

    @Transaction
    @Query("SELECT * FROM PostEntity")
    fun loadAll(): Flow<List<Post>>

}
