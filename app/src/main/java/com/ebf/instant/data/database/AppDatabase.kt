package com.ebf.instant.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebf.instant.data.database.converter.DateConverter
import com.ebf.instant.data.database.dao.CommentDao
import com.ebf.instant.data.database.dao.LikeDao
import com.ebf.instant.data.database.dao.PostDao
import com.ebf.instant.data.database.dao.UserDao
import com.ebf.instant.model.Comment
import com.ebf.instant.model.Like
import com.ebf.instant.model.Post
import com.ebf.instant.model.User

@Database(
    entities = [Post::class, User::class, Comment::class, Like::class],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao

    companion object {

        /**
         * Initialize [AppDatabase]
         * @param context the applicationContext
         * @param dbName the name of the database
         */
        fun init(context: Context, dbName: String = "Instant.db"): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, dbName).build()

    }

}
