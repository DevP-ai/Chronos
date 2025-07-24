package com.dev.ai.app.chronos.data.uistate

data class ReminderUiState(
    val id: String = "",
    val title: String = "",
    val dateTime: Long = 0L,
    val notes: String = "",
    val imageUrl: String? = null
)