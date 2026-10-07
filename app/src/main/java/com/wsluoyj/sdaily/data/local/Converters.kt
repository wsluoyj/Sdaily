package com.wsluoyj.sdaily.data.local

import androidx.room.TypeConverter
import com.wsluoyj.sdaily.data.local.entity.RecordMethod

class Converters {
    @TypeConverter
    fun methodToString(method: RecordMethod?): String? = method?.name

    @TypeConverter
    fun stringToMethod(value: String?): RecordMethod? =
        value?.let { runCatching { RecordMethod.valueOf(it) }.getOrNull() }
}
