package com.example.calmtask.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calmtask.data.model.Meeting
import com.example.calmtask.data.model.Task
import com.example.calmtask.ui.components.CalendarGrid
import com.example.calmtask.ui.components.SectionCard
import com.example.calmtask.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    tasks: List<Task>,
    meetings: List<Meeting>,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    onAddTask: (LocalDate) -> Unit,
    onAddMeeting: (LocalDate) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onDeleteMeeting: (Meeting) -> Unit
) {
    val taskDates = remember(tasks) { tasks.map { LocalDate.parse(it.dueDate, DateTimeFormatter.ISO_LOCAL_DATE) }.toSet() }
    val meetingDates = remember(meetings) { meetings.map { LocalDate.ofEpochDay(it.dateTime / 86400000) }.toSet() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val selectedDayTasks = remember(selectedDate, tasks) { tasks.filter { it.dueDate == selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE) } }
    val selectedDayMeetings = remember(selectedDate, meetings) { meetings.filter { LocalDate.ofEpochDay(it.dateTime / 86400000) == selectedDate } }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        CalendarGrid(
            selectedDate = selectedDate,
            onDateSelected = { onDateSelected(it); showBottomSheet = true },
            taskDates = taskDates,
            meetingDates = meetingDates
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { onAddTask(selectedDate) }) { Text("Add Task") }
            Button(onClick = { onAddMeeting(selectedDate) }) { Text("Add Meeting") }
        }
        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, containerColor = SurfaceDark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}", color = Color.White, fontWeight = FontWeight.Bold)
                    if (selectedDayTasks.isNotEmpty()) {
                        Text("Tasks", color = TextSecondary)
                        selectedDayTasks.forEach { task ->
                            Row(Modifier.fillMaxWidth().padding(vertical=4.dp), Arrangement.SpaceBetween) {
                                Text(task.title, color = Color.White)
                                Text(task.dueTime, color = TextSecondary)
                            }
                        }
                    }
                    if (selectedDayMeetings.isNotEmpty()) {
                        Text("Meetings", color = TextSecondary)
                        selectedDayMeetings.forEach { meeting ->
                            Row(Modifier.fillMaxWidth().padding(vertical=4.dp), Arrangement.SpaceBetween) {
                                Text(meeting.title, color = Color.White)
                                Text(meeting.location, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
