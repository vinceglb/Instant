package com.ebf.instant.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebf.instant.local.converter.ZonedDateTimeConverter
import com.ebf.instant.local.dao.PostDao
import com.ebf.instant.model.Post

@Database(entities = [Post::class], version = 1)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

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
