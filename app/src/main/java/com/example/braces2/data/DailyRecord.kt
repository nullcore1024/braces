package com.example.braces2.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "dailyrecord", indices = [Index(value = ["date", "planId"], unique = true)])
data class DailyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val planId: Long,
    val completed: Boolean,
    val direction: CorrectionDirection,
    val notes: String = ""
)

// 用于UI显示的数据类
data class CalendarDay(
    val date: LocalDate,
    val direction: CorrectionDirection,
    val completed: Boolean,
    val isToday: Boolean
)