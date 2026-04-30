package com.example.calmtask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarGrid(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    taskDates: Set<LocalDate>,
    meetingDates: Set<LocalDate>
) {
    val yearMonth = YearMonth.from(selectedDate)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value // 1=Mon, 7=Sun

    Column(modifier = Modifier.padding(16.dp)) {
        // Header with month/year
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + yearMonth.year,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Day labels
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color(0xFF8B8FA8),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Calendar grid
        var dayCounter = 1
        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 1..7) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else if (dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = yearMonth.atDay(dayCounter)
                        val isSelected = date == selectedDate
                        val hasTask = taskDates.contains(date)
                        val hasMeeting = meetingDates.contains(date)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF4F7DF3) else Color.Transparent)
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayCounter.toString(),
                                    color = if (isSelected) Color.White else Color(0xFFCCCCCC),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (hasTask || hasMeeting) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        if (hasTask) Box(modifier = Modifier.size(4.dp).background(Color.Green, CircleShape))
                                        if (hasMeeting) Box(modifier = Modifier.size(4.dp).background(Color.Blue, CircleShape))
                                    }
                                }
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
