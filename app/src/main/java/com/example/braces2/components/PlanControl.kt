package com.example.braces2.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.annotation.OptIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braces2.data.CorrectionPlan
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.DatePicker as AndroidDatePicker
import androidx.compose.material3.AlertDialog

@Composable
fun PlanControl(
    latestPlan: CorrectionPlan?, 
    forwardCount: Int,
    backwardCount: Int,
    onStartPlan: (LocalDate, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val calendar = Calendar.getInstance()
    calendar.time = java.util.Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

    Column(modifier = modifier) {
        if (latestPlan != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFF5F5F5)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "当前计划:", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "开始日期: ${latestPlan.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}", fontSize = 13.sp)
                    Text(text = "正方向次数: ${latestPlan.forwardCount}", fontSize = 13.sp)
                    Text(text = "反方向次数: ${latestPlan.backwardCount}", fontSize = 13.sp)
                    Text(text = "周期长度: ${latestPlan.cycleLength}天", fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "开始日期:")
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text(text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            }
            Button(
                onClick = { onStartPlan(selectedDate, forwardCount, backwardCount) }
            ) {
                Text(text = "开始计划")
            }
        }

        if (showDatePicker) {
            AlertDialog(
                onDismissRequest = { showDatePicker = false },
                title = { Text(text = "选择开始日期") },
                text = {
                    AndroidView(factory = { context ->
                        AndroidDatePicker(context).apply {
                            // 设置当前日期
                            init(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ) { view, year, month, dayOfMonth ->
                                // 更新日历对象
                                calendar.set(year, month, dayOfMonth)
                            }
                            // 设置星期的第一天为星期一
                            firstDayOfWeek = Calendar.MONDAY
                        }
                    })
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDatePicker = false
                            // 更新选中的日期
                            selectedDate = LocalDate.of(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1, // 注意：Calendar的月份是0-based
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                        }
                    ) {
                        Text(text = "确定")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(text = "取消")
                    }
                }
            )
        }
    }
}