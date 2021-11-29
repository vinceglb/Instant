package com.ebf.instant.data.database.converter

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromZonedDateTime(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromLong(value: Long): Date {
        return Date(value)
    }
}
