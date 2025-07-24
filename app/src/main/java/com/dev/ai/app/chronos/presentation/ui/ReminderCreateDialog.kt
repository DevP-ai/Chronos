package com.dev.ai.app.chronos.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.ai.app.chronos.presentation.viewModel.ReminderViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar


@Composable
fun ReminderCreateDialog(
    viewModel: ReminderViewModel = hiltViewModel(),
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val calendar = remember {
        Calendar.getInstance().apply {
            timeInMillis = if(state.dateTime == 0L){
                System.currentTimeMillis()+60+1000// Default to 1 min from now
            }else{
                state.dateTime
            }
        }
    }

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // Ensure the selected date is not in the past
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.timeInMillis = System.currentTimeMillis() + 60 * 1000
                }
                viewModel.onDateTimeChange(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }
    }



    AlertDialog(
        title = {Text("Add Reminder")},
        text = {
            Column (
                modifier = Modifier
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    label = {Text(text = "Title")}
                )
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = viewModel::onNoteChange,
                    label = {Text(text = "Notes (optional)")}
                )

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    Button(onClick = {datePickerDialog.show()}) { Text("Pick Date") }
                    Button(onClick = {}) { Text("Pick Time") }
                }
                Text("Date/Time: ")
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSave()
                }
            ) {
                Text(text = "save")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview
@Composable
fun ReminderCreateDialogPreview(modifier: Modifier = Modifier) {
    ReminderCreateDialog(
        onSave = {},
        onDismiss = {}
    )
}