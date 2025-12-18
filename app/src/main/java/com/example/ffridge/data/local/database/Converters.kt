package com.example.ffridge.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // String List Converter
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null) {
            "[]"
        } else {
            gson.toJson(value)
        }
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Long to Date String (optional helper)
    @TypeConverter
    fun fromTimestamp(value: Long?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toTimestamp(value: String?): Long? {
        return value?.toLongOrNull()
    }
}
