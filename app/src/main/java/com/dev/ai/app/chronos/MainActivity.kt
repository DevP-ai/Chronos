package com.dev.ai.app.chronos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.ai.app.chronos.presentation.ui.AuthActivity
import com.dev.ai.app.chronos.presentation.ui.ReminderScreen
import com.dev.ai.app.chronos.ui.theme.ChronosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChronosTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "reminders"){
                    composable("reminders") {
                        ReminderScreen(
                            onLogout = {
                                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}
