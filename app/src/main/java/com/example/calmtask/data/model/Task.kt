package com.example.calmtask.data.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val dueDate: String, // yyyy-MM-dd
    val dueTime: String, // HH:mm
    val colorHex: String,
    val status: String = "active", // active, completed, skipped, later
    val repeatDaily: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
