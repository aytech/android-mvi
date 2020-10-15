package com.oleg.androidmvi.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class GenreIdConverter {

    @TypeConverter
    fun stringToGenreList(data: String?): List<Int> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun genreListToString(genreIds: List<Int>?): String {
        if (genreIds == null) {
            return Gson().toJson(Collections.emptyList<Int>())
        }
        return Gson().toJson(genreIds)
    }

}
