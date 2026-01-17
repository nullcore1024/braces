package com.example.braces2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CorrectionPlan::class, DailyRecord::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun correctionPlanDao(): CorrectionPlanDao
    abstract fun dailyRecordDao(): DailyRecordDao
}