package com.example.braces2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "correctionplan")
data class CorrectionPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: LocalDate,
    val forwardCount: Int,
    val backwardCount: Int
) {
    val cycleLength: Int
        get() = forwardCount + backwardCount

    fun getDirectionForDate(date: LocalDate): CorrectionDirection {
        if (date.isBefore(startDate)) {
            return CorrectionDirection.NONE
        }

        // 安全检查，防止除以0
        if (cycleLength <= 0) {
            return CorrectionDirection.NONE
        }

        val daysSinceStart = startDate.datesUntil(date.plusDays(1)).count() - 1
        val cyclePosition = daysSinceStart % cycleLength

        return if (cyclePosition < forwardCount) {
            CorrectionDirection.FORWARD
        } else {
            CorrectionDirection.BACKWARD
        }
    }
}

enum class CorrectionDirection {
    NONE,
    FORWARD,
    BACKWARD
}