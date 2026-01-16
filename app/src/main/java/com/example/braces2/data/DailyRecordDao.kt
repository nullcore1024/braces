package com.example.braces2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate

@Dao
interface DailyRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DailyRecord)

    @Delete
    suspend fun delete(record: DailyRecord)

    @Query("SELECT * FROM dailyrecord WHERE date = :date")
    suspend fun getRecordByDate(date: LocalDate): DailyRecord?

    @Query("SELECT * FROM dailyrecord WHERE planId = :planId")
    suspend fun getRecordsByPlanId(planId: Long): List<DailyRecord>

    @Query("SELECT * FROM dailyrecord WHERE date >= :startDate AND date <= :endDate")
    suspend fun getRecordsInRange(startDate: LocalDate, endDate: LocalDate): List<DailyRecord>
}