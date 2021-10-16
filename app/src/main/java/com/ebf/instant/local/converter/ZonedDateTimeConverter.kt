package com.ebf.instant.local.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromZonedDateTime(date: ZonedDateTime): Long {
        return date.toInstant().toEpochMilli()
    }

    @TypeConverter
    fun fromLong(value: Long): ZonedDateTime {
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault())
    }
}
