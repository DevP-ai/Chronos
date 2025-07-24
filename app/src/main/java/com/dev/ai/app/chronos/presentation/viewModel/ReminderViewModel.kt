package com.dev.ai.app.chronos.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.ai.app.chronos.data.uistate.ReminderUiState
import com.dev.ai.app.chronos.domain.model.Reminder
import com.dev.ai.app.chronos.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState

    val reminders: StateFlow<List<Reminder>> = reminderRepository.getReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}