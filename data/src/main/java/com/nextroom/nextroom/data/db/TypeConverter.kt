package com.nextroom.nextroom.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import java.lang.reflect.Type


class TypeConverter {
    @TypeConverter
    fun hintIdsToJson(hints: Set<Int>): String {
        return Gson().toJson(hints)
    }

    @TypeConverter
    fun jsonToIds(json: String): Set<Int> {
        return Gson().fromJson(json, Array<Int>::class.java).toSet()
    }

    @TypeConverter
    fun jsonToList(value: String): List<String> {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: List<String?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun themeImageCustomInfoToJson(themeImageCustomInfo: ThemeImageCustomInfo?): String? {
        if (themeImageCustomInfo == null) return null
        return Gson().toJson(themeImageCustomInfo)
    }

    @TypeConverter
    fun jsonToThemeImageCustomInfo(json: String?): ThemeImageCustomInfo? {
        if (json == null) return null
        return Gson().fromJson(json, ThemeImageCustomInfo::class.java)
    }
}
