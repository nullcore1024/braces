package com.example.braces2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
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
    var currentTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("当前计划", "计划列表")
    var allPlans by remember { mutableStateOf<List<CorrectionPlan>?>(null) }
    
    // 在Composable函数顶层获取CoroutineScope
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 观察最新计划变化
    DisposableEffect(lifecycleOwner) {
        val observer = Observer<CorrectionPlan?> {plan ->
            latestPlan = plan
        }
        repository.latestPlan.observe(lifecycleOwner, observer)
        
        // 初始加载
        repository.latestPlan.value?.let { latestPlan = it }
        
        // 清理函数
        onDispose { repository.latestPlan.removeObserver(observer) }
    }

    // 监听latestPlan变化，重新加载日历数据
    LaunchedEffect(latestPlan) {
        try {
            val startDate = LocalDate.now().minusMonths(3)
            val endDate = LocalDate.now().plusMonths(3)
            val records = repository.getRecordsInRange(startDate, endDate)
            val allPlans = repository.getAllPlans()?.sortedBy { it.startDate }
            val days = mutableListOf<CalendarDay>()
            var currentDate = startDate

            while (!currentDate.isAfter(endDate)) {
                val record = records.find { it.date == currentDate }
                val direction = repository.getDirectionForDate(currentDate)
                
                // 根据矫正方向设置不同底色
                val color = when (direction) {
                    CorrectionDirection.FORWARD -> "#FFCDD2" // 红色底色
                    CorrectionDirection.BACKWARD -> "#C8E6C9" // 绿色底色
                    else -> null // 无方向则不渲染底色
                }
                
                days.add(
                    CalendarDay(
                        date = currentDate,
                        direction = direction,
                        completed = record?.completed ?: false,
                        isToday = currentDate == LocalDate.now(),
                        color = color
                    )
                )
                currentDate = currentDate.plusDays(1)
            }
            calendarDays = days
        } catch (e: Exception) {
            // 处理异常，防止应用闪退
            e.printStackTrace()
            // 重置日历数据，显示安全的默认状态
            calendarDays = listOf(
                CalendarDay(
                    date = LocalDate.now(),
                    direction = CorrectionDirection.NONE,
                    completed = false,
                    isToday = true,
                    color = null
                )
            )
        }
    }

    // 加载所有计划
    LaunchedEffect(currentTab) {
        if (currentTab == 1) {
            scope.launch {
                try {
                    allPlans = repository.getAllPlans()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 加载日历数据 - 当selectedDate变化时，只更新选中状态，不重新加载所有数据
    LaunchedEffect(selectedDate) {
        // 这里不需要重新加载所有日历数据，只需要确保选中状态正确
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // 底部标签栏
        TabRow(selectedTabIndex = currentTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = { Text(title) }
                )
            }
        }

        // 标签内容
        when (currentTab) {
            0 -> CurrentPlanContent(
                repository,
                latestPlan,
                forwardCount,
                backwardCount,
                calendarDays,
                selectedDate,
                showCompletionDialog,
                completionStatus,
                onForwardCountChange = { forwardCount = it },
                onBackwardCountChange = { backwardCount = it },
                onSelectedDateChange = { selectedDate = it },
                onShowCompletionDialogChange = { showCompletionDialog = it },
                onCompletionStatusChange = { completionStatus = it },
                onCalendarDaysChange = { calendarDays = it }
            )
            1 -> PlanListContent(allPlans)
        }
    }
}

@Composable
fun CurrentPlanContent(
    repository: AppRepository,
    latestPlan: CorrectionPlan?,
    forwardCount: Int,
    backwardCount: Int,
    calendarDays: List<CalendarDay>,
    selectedDate: LocalDate,
    showCompletionDialog: Boolean,
    completionStatus: String,
    onForwardCountChange: (Int) -> Unit,
    onBackwardCountChange: (Int) -> Unit,
    onSelectedDateChange: (LocalDate) -> Unit,
    onShowCompletionDialogChange: (Boolean) -> Unit,
    onCompletionStatusChange: (String) -> Unit,
    onCalendarDaysChange: (List<CalendarDay>) -> Unit
) {
    // 在Composable函数顶层获取CoroutineScope
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // 应用标题
        Text(
            text = "牙齿纠正进展跟踪",
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 计划控制组件
        PlanControl(
            latestPlan = latestPlan,
            forwardCount = forwardCount,
            backwardCount = backwardCount,
            onStartPlan = { date, forward, backward ->
                try {
                    val plan = CorrectionPlan(
                        startDate = date,
                        forwardCount = forward,
                        backwardCount = backward
                    )
                    repository.insertPlan(plan)
                } catch (e: Exception) {
                    // 处理异常，防止应用闪退
                    e.printStackTrace()
                    // 可以添加一个错误提示给用户
                    onCompletionStatusChange("创建计划失败，请重试")
                    onShowCompletionDialogChange(true)
                }
            },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 次数选择器
        CountSelectors(
            forwardCount = forwardCount,
            backwardCount = backwardCount,
            onForwardCountChange = onForwardCountChange,
            onBackwardCountChange = onBackwardCountChange,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 日历视图
        CalendarView(
            currentDate = selectedDate,
            calendarDays = calendarDays,
            onDateSelected = {
                onSelectedDateChange(it)
            },
            modifier = Modifier.weight(1f)
        )
        
        // 打卡按钮
        Button(
            onClick = {
                val currentPlan = latestPlan
                val direction = repository.getDirectionForDate(selectedDate)
                if (direction != CorrectionDirection.NONE && currentPlan != null) {
                    // 保存打卡记录
                    val record = DailyRecord(
                        date = selectedDate,
                        planId = currentPlan.id,
                        completed = true,
                        direction = direction
                    )
                    scope.launch {
                        try {
                            repository.saveRecord(record)
                            // 更新日历数据
                            val updatedDays = calendarDays.map {
                                if (it.date == selectedDate) {
                                    it.copy(completed = true)
                                } else {
                                    it
                                }
                            }
                            onCalendarDaysChange(updatedDays)
                            onCompletionStatusChange("已完成${selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))}的打卡")
                            onShowCompletionDialogChange(true)
                        } catch (e: Exception) {
                            // 处理异常，防止应用闪退
                            e.printStackTrace()
                            onCompletionStatusChange("打卡失败，请重试")
                            onShowCompletionDialogChange(true)
                        }
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
                onDismissRequest = { onShowCompletionDialogChange(false) },
                title = { Text(text = "打卡完成") },
                text = { Text(text = completionStatus) },
                confirmButton = {
                    Button(onClick = { onShowCompletionDialogChange(false) }) {
                        Text(text = "确定")
                    }
                }
            )
        }
    }
}

@Composable
fun PlanListContent(plans: List<CorrectionPlan>?)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "计划列表",
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        if (plans.isNullOrEmpty()) {
            Text(
                text = "暂无计划",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .wrapContentSize()
            )
        } else {
            androidx.compose.foundation.lazy.LazyColumn {
                items(plans) {
                    PlanCard(plan = it)
                }
            }
        }
    }
}

@Composable
fun PlanCard(plan: CorrectionPlan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = plan.startDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${plan.forwardCount}正${plan.backwardCount}反",
                fontSize = 14.sp
            )
            Text(
                text = "周期长度: ${plan.cycleLength}天",
                fontSize = 14.sp
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