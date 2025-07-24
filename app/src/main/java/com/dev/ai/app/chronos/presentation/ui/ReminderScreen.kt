package com.dev.ai.app.chronos.presentation.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dev.ai.app.chronos.R
import com.dev.ai.app.chronos.data.uistate.GreetingState
import com.dev.ai.app.chronos.domain.model.Reminder
import com.dev.ai.app.chronos.presentation.ui.component.ReminderItem
import com.dev.ai.app.chronos.presentation.ui.component.ShimmerEffect
import com.dev.ai.app.chronos.presentation.ui.component.ThemeSwitcher
import com.dev.ai.app.chronos.presentation.viewModel.ReminderViewModel
import com.dev.ai.app.chronos.ui.theme.ChronosTheme
import com.dev.ai.app.chronos.util.NetworkUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()
    val greetingState by viewModel.greetingState.collectAsState()

    var showShimmer by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showShimmer = false
    }
    val userInfo by viewModel.userInfo.collectAsState()
    var showProfile by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(false) }
    var promptText by remember { mutableStateOf("") }
    var isFetchingGreeting by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val isConnected by NetworkUtils.observeNetworkStatus(context).collectAsState(initial = false)


    // Handle Implicit Intent to Share Message
    when (greetingState) {
        is GreetingState.Success -> {
            val message = (greetingState as GreetingState.Success).message
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share AI Message"))
            viewModel.resetGreetingState()
            isFetchingGreeting = false
        }
        is GreetingState.Error -> {
            viewModel.resetGreetingState()
            isFetchingGreeting = false
        }
        is GreetingState.Loading -> {
            isFetchingGreeting = true
        }
        else -> {}
    }

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
                        IconButton(onClick = { showProfile = true }) {
                            Icon(
                                Icons.Default.AccountCircle,
                                modifier = Modifier.size(36.dp),
                                contentDescription = "User Menu"
                            )
                        }
                        DropdownMenu(
                            expanded = showProfile,
                            onDismissRequest = { showProfile = false }
                        ) {
                            Text("Profile")
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        userInfo?.displayName
                                            ?: userInfo?.email
                                            ?: "Guest"
                                    )
                                },
                                onClick = { },
                                enabled = false
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showProfile = false
                                    viewModel.logout()
                                    onLogout()
                                }
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
            }
        ){padding->
            if(isConnected){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
                ) {
                    Text(
                        text = "Share AI Message",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                    if (isFetchingGreeting) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_animation))
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 8.dp)
                        )
                    }else{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promptText,
                                onValueChange = { promptText = it },
                                label = { Text("Enter prompt (e.g., birthday wish)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (promptText.isNotEmpty()) {
                                        viewModel.fetchAIGreeting(promptText)
                                        isFetchingGreeting = true
                                        promptText = ""
                                    }
                                },
                                enabled = promptText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Share AI Message"
                                )
                            }
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Reminders",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Reminder",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable{
                                    viewModel.editReminder(Reminder())
                                    showDialog = true
                                }
                        )
                    }
                    if(showShimmer){
                        ShimmerEffect()
                    }else if(reminders.isEmpty()){
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hello_animation))
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            )
                            Text(
                                text = "No Reminder, click + to add reminder",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    else{
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
                                    onDelete = {
                                        viewModel.deleteReminder(reminder.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_internet))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                    Text(
                        text = "No Internet Connection",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ReminderScreenPreview(modifier: Modifier = Modifier) {
    ReminderScreen(
        onLogout = {}
    )
}