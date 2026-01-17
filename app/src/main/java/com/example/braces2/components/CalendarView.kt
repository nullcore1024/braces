package com.example.braces2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braces2.data.CalendarDay
import com.example.braces2.data.CorrectionDirection
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun CalendarView(
    currentDate: LocalDate,
    calendarDays: List<CalendarDay>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(currentDate) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text(text = "<")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text(text = ">")
            }
        }

        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("一", "二", "三", "四", "五", "六", "日").forEach {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .background(Color.LightGray)
                        .clickable {}
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // 日历网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(getCalendarDaysForMonth(currentMonth, calendarDays)) {
                CalendarDayCell(
                    day = it,
                    onClick = { onDateSelected(it.date) }
                )
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 首先使用day.color，如果没有则使用默认的颜色逻辑
    // 优先使用day.color，如果没有则使用默认颜色逻辑
    val backgroundColor = day.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: when {
        day.isToday -> Color(0xFFE3F2FD)
        else -> Color.White
    }

    val statusText = when (day.direction) {
        CorrectionDirection.FORWARD -> "正"
        CorrectionDirection.BACKWARD -> "反"
        else -> ""
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = if (day.isToday) CardDefaults.cardElevation(defaultElevation = 8.dp) else CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // ✓图像显示在日期上面
            if (day.completed) {
                Text(
                    text = "✓",
                    fontSize = 24.sp,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    fontSize = 16.sp,
                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                )
                if (statusText.isNotEmpty()) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

private fun getCalendarDaysForMonth(month: LocalDate, existingDays: List<CalendarDay>): List<CalendarDay> {
    val year = month.year
    val monthValue = month.monthValue

    val firstDayOfMonth = LocalDate.of(year, monthValue, 1)
    val lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())

    // 确保从周一开始，到周日结束
    // 计算当月的第一个周一或之前的周一
    var startDate = firstDayOfMonth
    while (startDate.dayOfWeek != DayOfWeek.MONDAY) {
        startDate = startDate.minusDays(1)
    }

    // 计算当月的最后一个周日或之后的周日
    var endDate = lastDayOfMonth
    while (endDate.dayOfWeek != DayOfWeek.SUNDAY) {
        endDate = endDate.plusDays(1)
    }

    val result = mutableListOf<CalendarDay>()
    var currentDate = startDate

    while (!currentDate.isAfter(endDate)) {
        val existingDay = existingDays.find { it.date == currentDate }
        if (existingDay != null) {
            result.add(existingDay)
        } else {
            result.add(
                CalendarDay(
                    date = currentDate,
                    direction = CorrectionDirection.NONE,
                    completed = false,
                    isToday = currentDate == LocalDate.now(),
                    color = null
                )
            )
        }
        currentDate = currentDate.plusDays(1)
    }

    return result
}