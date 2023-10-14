package com.nextroom.nextroom.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson

class TypeConverter {
    @TypeConverter
    fun hintIdsToJson(hints: Set<Int>): String {
        return Gson().toJson(hints)
    }

    @TypeConverter
    fun jsonToIds(json: String): Set<Int> {
        return Gson().fromJson(json, Array<Int>::class.java).toSet()
    }
}
