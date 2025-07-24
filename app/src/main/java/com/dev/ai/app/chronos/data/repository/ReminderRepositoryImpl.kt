package com.dev.ai.app.chronos.data.repository

import com.dev.ai.app.chronos.domain.model.Reminder
import com.dev.ai.app.chronos.domain.repository.ReminderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ReminderRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth : FirebaseAuth
): ReminderRepository {

    private fun getReminderCollection() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: throw IllegalArgumentException("User not found"))
        .collection("reminders")

    override fun getReminders(): Flow<List<Reminder>> = callbackFlow {
        val listener = getReminderCollection().addSnapshotListener { snapshot,_->
            val reminder = snapshot?.documents?.mapNotNull {
                it.toObject(Reminder::class.java)?.copy(id = it.id)
            }?: emptyList()

            trySend(reminder)
        }
        awaitClose{listener.remove()}
    }

    override suspend fun addReminder(reminder: Reminder) {
        getReminderCollection().add(reminder)
    }

    override suspend fun deleteReminder(reminderId: String) {
        getReminderCollection().document(reminderId).delete()
    }

    override suspend fun updateReminder(reminder: Reminder) {
        getReminderCollection().document(reminder.id).set(reminder)
    }
}