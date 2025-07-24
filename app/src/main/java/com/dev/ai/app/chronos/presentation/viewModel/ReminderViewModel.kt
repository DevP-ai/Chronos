package com.dev.ai.app.chronos.presentation.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.ai.app.chronos.data.uistate.GreetingState
import com.dev.ai.app.chronos.data.uistate.ReminderUiState
import com.dev.ai.app.chronos.data.usecase.GetAIGreetingUseCase
import com.dev.ai.app.chronos.domain.model.Reminder
import com.dev.ai.app.chronos.domain.model.UserInfo
import com.dev.ai.app.chronos.domain.repository.ReminderRepository
import com.dev.ai.app.chronos.receiver.ReminderReceiver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.compareTo
import kotlin.hashCode

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val firebaseAuth: FirebaseAuth,
    private val getAIGreetingUseCase: GetAIGreetingUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState

    private val _greetingState = MutableStateFlow<GreetingState>(GreetingState.Idle)
    val greetingState: StateFlow<GreetingState> = _greetingState

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    val reminders: StateFlow<List<Reminder>> = reminderRepository.getReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun updateUserInfo() {
        val currentUser = firebaseAuth.currentUser
        _userInfo.value = currentUser?.let {
            UserInfo(
                displayName = it.displayName,
                email = it.email
            )
        }
    }

    fun onTitleChange(title: String){
        _uiState.update { it.copy(title = title) }
    }
    fun onNoteChange(notes: String){
        _uiState.update { it.copy(notes = notes) }
    }

    fun onDateTimeChange(dateTime: Long) {
        _uiState.update { it.copy(dateTime = dateTime) }
    }
    fun onImageUrlChange(imageUrl: String?) {
        _uiState.update { it.copy(imageUrl = imageUrl) }
    }
    fun saveReminder(){
        val state = _uiState.value
        val reminder = Reminder(
            id = state.id.ifEmpty { UUID.randomUUID().toString() },
            title = state.title,
            notes = state.notes,
            dateTime = state.dateTime,
            imageUrl = state.imageUrl
        )

        viewModelScope.launch {
            if(state.id.isEmpty()){
                reminderRepository.addReminder(reminder)
            }else{
                reminderRepository.updateReminder(reminder)
            }
            scheduleReminderNotification(reminder)
            _uiState.value = ReminderUiState()
        }
    }

    fun editReminder(reminder: Reminder){
        _uiState.value = ReminderUiState(
            id = reminder.id,
            title = reminder.title,
            notes = reminder.notes?:"",
            dateTime = reminder.dateTime,
            imageUrl = reminder.imageUrl
        )
    }

    fun deleteReminder(reminderId: String){
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminderId)
            // Cancel the scheduled alarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = "com.dev.ai.app.chronos.REMINDER_ACTION"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
                Log.d("ReminderViewModel", "Canceled alarm for reminder ID: $reminderId")
            }
            Log.d("ReminderViewModel", "Deleted reminder ID: $reminderId")
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            _userInfo.value = null
        }
    }

    fun fetchAIGreeting(prompt: String) {
        viewModelScope.launch {
            _greetingState.value = GreetingState.Loading
            val result = getAIGreetingUseCase(prompt)
            _greetingState.value = when {
                result.isSuccess -> GreetingState.Success(result.getOrNull() ?: "")
                result.isFailure -> GreetingState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                else -> GreetingState.Idle
            }
        }
    }

    fun resetGreetingState() {
        _greetingState.value = GreetingState.Idle
    }

    private fun scheduleReminderNotification(reminder: Reminder) {
        val currentTime = System.currentTimeMillis()
        val delay = reminder.dateTime - currentTime
        Log.d("ReminderViewModel", "Scheduling reminder: ${reminder.title}, ID: ${reminder.id}, Delay: $delay ms")

        if (delay > 0) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = "com.dev.ai.app.chronos.REMINDER_ACTION"
                putExtra("title", reminder.title)
                putExtra("notes", reminder.notes)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.dateTime,
                    pendingIntent
                )
                Log.d("ReminderViewModel", "Alarm scheduled for reminder ID: ${reminder.id} at ${reminder.dateTime}")
            } catch (e: SecurityException) {
                Log.e("ReminderViewModel", "Failed to schedule alarm: ${e.message}")
            }
        } else {
            Log.w("ReminderViewModel", "Cannot schedule reminder ${reminder.title}: Time is in the past or invalid (delay: $delay)")
        }
    }

}