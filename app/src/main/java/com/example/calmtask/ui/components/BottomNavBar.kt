package com.example.calmtask.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.calmtask.ui.theme.SurfaceDark

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, "home"),
    BottomNavItem("Calendar", Icons.Default.DateRange, "calendar"),
    BottomNavItem("Achievements", Icons.Default.Star, "achievements"),
    BottomNavItem("Settings", Icons.Default.Settings, "settings"),
    BottomNavItem("Voice Chat", Icons.Default.Mic, "voice_chat")
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = SurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, fontSize = MaterialTheme.typography.labelMedium.fontSize) },
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4F7DF3),
                        unselectedIconColor = Color(0xFF8B8FA8),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
