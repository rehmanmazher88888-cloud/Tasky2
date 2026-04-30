package com.example.calmtask.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmtask.data.model.Task
import com.example.calmtask.ui.theme.*

@Composable
fun TaskCard(
    task: Task,
    onDone: () -> Unit,
    onLater: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = when (task.colorHex) {
        "#FF6B6B" -> CoralGradient
        "#11998E" -> TealGradient
        "#834D9B" -> PurpleGradient
        "#2193B0" -> BlueGradient
        "#F7971E" -> GoldGradient
        else -> listOf(Color(0xFF4F7DF3), Color(0xFF7B9FFF))
    }
    val elevation by animateDpAsState(targetValue = if (task.status == "completed") 2.dp else 6.dp, label = "elev")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(gradient),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(task.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                if (task.description.isNotEmpty())
                    Text(task.description, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Due ${task.dueTime}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(onClick = onDone, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White)
                        }
                        IconButton(onClick = onLater, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Star, contentDescription = "Later", tint = Color.White)
                        }
                        IconButton(onClick = onSkip, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Skip", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
