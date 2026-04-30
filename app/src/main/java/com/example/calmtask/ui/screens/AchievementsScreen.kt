package com.example.calmtask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calmtask.ui.components.ProgressRing
import com.example.calmtask.ui.theme.AccentDefault
import com.example.calmtask.ui.theme.TextSecondary

@Composable
fun AchievementsScreen(
    averageCompletion: Float,
    streak: Int,
    weeklyData: List<Float>,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            "Achievements",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(
                progress = averageCompletion,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "${(averageCompletion * 100).toInt()}% Average",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text("$streak days in a row 🔥", color = Color.White)
                val message = when {
                    streak == 0 -> "Every day is a fresh start 🌱"
                    streak < 3 -> "You're building momentum! 💪"
                    streak < 7 -> "One week strong! 🔥"
                    else -> "You're unstoppable 🏆"
                }
                Text(message, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("This Week", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyData.forEachIndexed { i, percent ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(percent * 100).toInt()}%",
                        color = TextSecondary,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(24.dp)
                            .height(80.dp * percent.coerceIn(0.05f, 1f))
                            .background(
                                AccentDefault,
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        when (i) {
                            0 -> "M"; 1 -> "T"; 2 -> "W"
                            3 -> "T"; 4 -> "F"; 5 -> "S"
                            6 -> "S"; else -> ""
                        },
                        color = TextSecondary,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onShare,
            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Share Achievements")
        }
    }
}
