package com.example.braces2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate

@Dao
interface CorrectionPlanDao {
    @Insert
    suspend fun insert(plan: CorrectionPlan): Long

    @Delete
    suspend fun delete(plan: CorrectionPlan)

    @Query("SELECT * FROM correctionplan ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestPlan(): CorrectionPlan?

    @Query("SELECT * FROM correctionplan WHERE id = :planId")
    suspend fun getPlanById(planId: Long): CorrectionPlan?
}