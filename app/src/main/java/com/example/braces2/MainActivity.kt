package com.example.braces2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braces2.components.CalendarView
import com.example.braces2.components.CountSelectors
import com.example.braces2.components.PlanControl
import com.example.braces2.data.AppRepository
import com.example.braces2.data.CalendarDay
import com.example.braces2.data.CorrectionDirection
import com.example.braces2.data.CorrectionPlan
import com.example.braces2.data.DailyRecord
import com.example.braces2.ui.theme.Braces2Theme
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = (application as BracesApplication).repository
        enableEdgeToEdge()
        setContent {
            Braces2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    BracesApp(
                        repository = repository,
                        modifier = Modifier.padding(it)
                    )
                }
            }
        }
    }
}

@Composable
fun BracesApp(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    // 从repository获取latestPlan
    var latestPlan by remember { mutableStateOf<CorrectionPlan?>(null) }
    var forwardCount by remember { mutableStateOf(2) }
    var backwardCount by remember { mutableStateOf(2) }
    var calendarDays by remember { mutableStateOf(listOf<CalendarDay>()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var completionStatus by remember { mutableStateOf("") }
    
    // 在Composable函数顶层获取CoroutineScope
    val scope = rememberCoroutineScope()

    // 加载最新计划
    LaunchedEffect(Unit) {
        val plan = repository.latestPlan.value
        latestPlan = plan
    }

    // 加载日历数据
    LaunchedEffect(latestPlan, selectedDate) {
        if (latestPlan != null) {
            // 生成当前月份前后3个月的日历数据
            val startDate = LocalDate.now().minusMonths(3)
            val endDate = LocalDate.now().plusMonths(3)
            
            val records = repository.getRecordsInRange(startDate, endDate)
            val days = mutableListOf<CalendarDay>()
            var currentDate = startDate

            while (!currentDate.isAfter(endDate)) {
                val record = records.find { it.date == currentDate }
                val direction = repository.getDirectionForDate(currentDate)
                
                days.add(
                    CalendarDay(
                        date = currentDate,
                        direction = direction,
                        completed = record?.completed ?: false,
                        isToday = currentDate == LocalDate.now()
                    )
                )
                currentDate = currentDate.plusDays(1)
            }
            calendarDays = days
        } else {
            // 没有计划时，只显示当前日期
            calendarDays = listOf(
                CalendarDay(
                    date = LocalDate.now(),
                    direction = CorrectionDirection.NONE,
                    completed = false,
                    isToday = true
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 应用标题
        Text(
            text = "牙齿纠正进展跟踪",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 计划控制组件
        PlanControl(
            latestPlan = latestPlan,
            forwardCount = forwardCount,
            backwardCount = backwardCount,
            onStartPlan = { date, forward, backward ->
                val plan = CorrectionPlan(
                    id = System.currentTimeMillis(),
                    startDate = date,
                    forwardCount = forward,
                    backwardCount = backward
                )
                repository.insertPlan(plan)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 次数选择器
        CountSelectors(
            forwardCount = forwardCount,
            backwardCount = backwardCount,
            onForwardCountChange = { forwardCount = it },
            onBackwardCountChange = { backwardCount = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 日历视图
        CalendarView(
            currentDate = selectedDate,
            calendarDays = calendarDays,
            onDateSelected = { date ->
                selectedDate = date
            },
            modifier = Modifier.weight(1f)
        )
        
        // 打卡按钮
        Button(
            onClick = {
                val direction = repository.getDirectionForDate(selectedDate)
                if (direction != CorrectionDirection.NONE && latestPlan != null) {
                    // 保存打卡记录
                    val record = DailyRecord(
                        date = selectedDate,
                        planId = latestPlan!!.id,
                        completed = true,
                        direction = direction
                    )
                    scope.launch {
                        repository.saveRecord(record)
                        // 更新日历数据
                        val updatedDays = calendarDays.map {
                            if (it.date == selectedDate) {
                                it.copy(completed = true)
                            } else {
                                it
                            }
                        }
                        calendarDays = updatedDays
                        completionStatus = "已完成${selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))}的打卡"
                        showCompletionDialog = true
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            Text(text = "打卡")
        }

        // 打卡完成对话框
        if (showCompletionDialog) {
            AlertDialog(
                onDismissRequest = { showCompletionDialog = false },
                title = { Text(text = "打卡完成") },
                text = { Text(text = completionStatus) },
                confirmButton = {
                    Button(onClick = { showCompletionDialog = false }) {
                        Text(text = "确定")
                    }
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//     Braces2Theme {
//         Greeting("Android")
//     }
// }