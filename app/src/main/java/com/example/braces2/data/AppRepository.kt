package com.example.braces2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            val plan = withContext(Dispatchers.IO) {
                database.correctionPlanDao().getLatestPlan()
            }
            _latestPlan.value = plan
        }
    }

    fun insertPlan(plan: CorrectionPlan) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                database.correctionPlanDao().insert(plan)
            }
            loadLatestPlan()
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
}