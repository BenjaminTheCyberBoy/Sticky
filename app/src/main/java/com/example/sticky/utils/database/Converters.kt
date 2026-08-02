package com.example.sticky.utils.database

import androidx.room3.ColumnTypeConverter
import kotlin.uuid.Uuid

class Converters {
    @ColumnTypeConverter
    fun fromUuid(uuid: Uuid?): String? = uuid?.toString()

    @ColumnTypeConverter
    fun toUuid(uuidString: String?): Uuid? = uuidString?.let { Uuid.parse(it) }
}
