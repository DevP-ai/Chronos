package com.dev.ai.app.chronos.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ReminderCreateDialog(
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        title = {Text("Add Reminder")},
        text = {
            Column (
                modifier = Modifier
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedTextField(
                    value ="",
                    onValueChange = {},
                    label = {Text(text = "Title")}
                )
                OutlinedTextField(
                    value ="",
                    onValueChange = {},
                    label = {Text(text = "Notes (optional)")}
                )

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    Button(onClick = {}) { Text("Pick Date") }
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