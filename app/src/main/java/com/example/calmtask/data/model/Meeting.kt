package com.example.calmtask.data.model

import java.util.UUID

data class Meeting(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val location: String = "",
    val dateTime: Long,
    val notes: String = "",
    val reminded2hr: Boolean = false,
    val reminded1day: Boolean = false
)
