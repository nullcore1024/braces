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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.braces2.data.CorrectionPlan
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    Column(modifier = modifier) {
        if (latestPlan != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFF5F5F5)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "当前计划:", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "开始日期: ${latestPlan.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
                    Text(text = "正方向次数: ${latestPlan.forwardCount}")
                    Text(text = "反方向次数: ${latestPlan.backwardCount}")
                    Text(text = "周期长度: ${latestPlan.cycleLength}天")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "开始日期:")
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onStartPlan(selectedDate, forwardCount, backwardCount) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "开始计划")
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showDatePicker = false
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                selectedDate = Instant.ofEpochMilli(selectedMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
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
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}