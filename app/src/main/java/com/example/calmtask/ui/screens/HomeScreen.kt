package com.example.calmtask.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmtask.data.model.Task
import com.example.calmtask.data.model.UserProfile
import com.example.calmtask.ui.components.ProgressRing
import com.example.calmtask.ui.components.TaskCard
import com.example.calmtask.ui.theme.AccentDefault
import com.example.calmtask.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    tasks: List<Task>,
    profile: UserProfile,
    onMarkDone: (Task) -> Unit,
    onMarkLater: (Task) -> Unit,
    onSkip: (Task) -> Unit,
    onAddTask: () -> Unit,
    completionPercent: Float,
    streak: Int
) {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
    val activeTasks = tasks.filter { it.status == "active" }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = AccentDefault
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Greeting
                if (profile.name.isNotBlank()) {
                    Text(
                        "Good morning, ${profile.name} 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "Good morning 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(today, color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))

                // Progress row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProgressRing(
                        progress = completionPercent,
                        modifier = Modifier.size(64.dp)
                    )
                    Column {
                        Text(
                            "${(completionPercent * 100).toInt()}% today",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "$streak day streak 🔥",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Today's Tasks",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (activeTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tasks for today 🎉\nTap + to add one",
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(activeTasks) { task ->
                    TaskCard(
                        task = task,
                        onDone = { onMarkDone(task) },
                        onLater = { onMarkLater(task) },
                        onSkip = { onSkip(task) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
