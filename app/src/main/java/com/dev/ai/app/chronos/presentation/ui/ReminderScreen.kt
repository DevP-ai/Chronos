package com.dev.ai.app.chronos.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.dev.ai.app.chronos.presentation.ui.component.ThemeSwitcher
import com.dev.ai.app.chronos.ui.theme.ChronosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    modifier: Modifier = Modifier
) {
   var isDarkTheme by remember { mutableStateOf(false) }

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
                                fontSize = 24.sp,
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
            }
        ){padding->

        }
    }
}

@Preview
@Composable
fun ReminderScreenPreview(modifier: Modifier = Modifier) {
    ReminderScreen()
}