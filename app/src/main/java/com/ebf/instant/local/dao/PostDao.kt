package com.ebf.instant.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebf.instant.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(posts: List<Post>)

    @Query("SELECT * FROM Post")
    fun loadAll(): Flow<List<Post>>

}
