package com.example.braces2.data

import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverter {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromCorrectionDirection(direction: CorrectionDirection?): String? {
        return direction?.name
    }

    @TypeConverter
    fun toCorrectionDirection(directionString: String?): CorrectionDirection? {
        return directionString?.let {
            try {
                CorrectionDirection.valueOf(it)
            } catch (e: IllegalArgumentException) {
                // 处理无效的方向值，返回默认值
                CorrectionDirection.NONE
            }
        }
    }
}