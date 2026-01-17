package com.example.braces2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AppRepository(private val database: AppDatabase) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _latestPlan = MutableLiveData<CorrectionPlan?>(null)
    val latestPlan: LiveData<CorrectionPlan?> = _latestPlan

    init {
        loadLatestPlan()
    }

    private fun loadLatestPlan() {
        coroutineScope.launch {
            try {
                val plan = withContext(Dispatchers.IO) {
                    database.correctionPlanDao().getLatestPlan()
                }
                _latestPlan.value = plan
            } catch (e: Exception) {
                e.printStackTrace()
                _latestPlan.value = null
            }
        }
    }

    fun insertPlan(plan: CorrectionPlan) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.correctionPlanDao().upsert(plan)
                }
                loadLatestPlan()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun upsertPlan(plan: CorrectionPlan) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.correctionPlanDao().upsert(plan)
                }
                loadLatestPlan()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getDirectionForDate(date: LocalDate): CorrectionDirection {
        val plan = _latestPlan.value
        return plan?.getDirectionForDate(date) ?: CorrectionDirection.NONE
    }

    suspend fun getRecordForDate(date: LocalDate): DailyRecord? {
        return withContext(Dispatchers.IO) {
            database.dailyRecordDao().getRecordByDate(date)
        }
    }

    suspend fun saveRecord(record: DailyRecord) {
        withContext(Dispatchers.IO) {
            database.dailyRecordDao().insert(record)
        }
    }

    suspend fun getRecordsInRange(startDate: LocalDate, endDate: LocalDate): List<DailyRecord> {
        return withContext(Dispatchers.IO) {
            database.dailyRecordDao().getRecordsInRange(startDate, endDate)
        }
    }

    suspend fun getAllPlans(): List<CorrectionPlan>? {
        return withContext(Dispatchers.IO) {
            database.correctionPlanDao().getAllPlans()
        }
    }
}