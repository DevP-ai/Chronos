package com.dev.ai.app.chronos.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.ai.app.chronos.presentation.ui.component.ReminderItem
import com.dev.ai.app.chronos.presentation.ui.component.ThemeSwitcher
import com.dev.ai.app.chronos.presentation.viewModel.ReminderViewModel
import com.dev.ai.app.chronos.ui.theme.ChronosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()

    var isDarkTheme by remember { mutableStateOf(false) }
    var promptText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    ChronosTheme(darkTheme = isDarkTheme){
        Scaffold (
            topBar = {
                TopAppBar(
                    title = {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                text = "Chronos",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text= "Your AI Agent",
                                fontSize = 16.sp
                            )
                        }
                    },
                    navigationIcon ={
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.AccountCircle,
                                modifier = Modifier.size(36.dp),
                                contentDescription = "Logout"
                            )
                        }
                    },
                    actions = {
                        ThemeSwitcher(
                            darkTheme = isDarkTheme,
                            onClick = { isDarkTheme = !isDarkTheme }
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    showDialog = true
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Reminder"
                    )
                }
            }
        ){padding->
            Column(
                  modifier = Modifier
                   .fillMaxWidth()
                   .padding(padding),

            ) {
                Text(
                    text = "Share AI Message",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        label = { Text("Enter prompt (e.g., birthday wish)") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 2.dp)
                    )
                    IconButton(
                        onClick = {
                            if (promptText.isNotEmpty()) {
                                promptText = ""
                            }
                        },
                        enabled = promptText.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Send,
                            contentDescription = "Share AI Message"
                        )
                    }
                }

                if(showDialog){
                    ReminderCreateDialog(
                        viewModel = viewModel,
                        onSave = {
                            viewModel.saveReminder()
                            showDialog = false
                        },
                        onDismiss = {
                            showDialog = false
                        }
                    )
                }
                Text(
                    text = "Reminders",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                LazyColumn {
                    items (reminders){reminder->
                        ReminderItem(
                            title = reminder.title,
                            notes = reminder.notes,
                            dateTime = reminder.dateTime,
                            imageUrl = reminder.imageUrl,
                            onEdit = {
                                viewModel.editReminder(reminder)
                                showDialog = true
                            },
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ReminderScreenPreview(modifier: Modifier = Modifier) {
    ReminderScreen()
}