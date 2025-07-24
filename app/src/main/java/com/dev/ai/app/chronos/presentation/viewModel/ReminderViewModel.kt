package com.dev.ai.app.chronos.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.ai.app.chronos.data.uistate.ReminderUiState
import com.dev.ai.app.chronos.domain.model.Reminder
import com.dev.ai.app.chronos.domain.model.UserInfo
import com.dev.ai.app.chronos.domain.repository.ReminderRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState

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
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            _userInfo.value = null
        }
    }
}