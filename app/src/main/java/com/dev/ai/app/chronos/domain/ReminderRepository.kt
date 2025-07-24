package com.dev.ai.app.chronos.domain

import com.dev.ai.app.chronos.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminders(): Flow<List<Reminder>>
    suspend fun addReminder(reminder: Reminder)
    suspend fun deleteReminder(reminderId: String)
    suspend fun updateReminder(reminder: Reminder)
}