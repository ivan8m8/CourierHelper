package io.github.ivan8m8.courierhelper.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class GsonConverter<T> {

    private val gson = Gson()

    @TypeConverter
    fun from(o: T): String {
        return gson.toJson(o)
    }

    @TypeConverter
    fun to(json: String): T {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(json, type)
    }
}