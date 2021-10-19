package com.ebf.instant.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebf.instant.local.converter.DateConverter
import com.ebf.instant.local.dao.PostDao
import com.ebf.instant.local.dao.UserDao
import com.ebf.instant.model.Post
import com.ebf.instant.model.User

@Database(entities = [Post::class, User::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao

    companion object {

        /**
         * Initialize [AppDatabase]
         * @param context the applicationContext
         * @param dbName the name of the database
         */
        fun init(context: Context, dbName: String = "Instant.db"): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                .build()

    }

}
