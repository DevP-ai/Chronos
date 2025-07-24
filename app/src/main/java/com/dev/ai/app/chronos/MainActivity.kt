package com.dev.ai.app.chronos

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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

        // Request POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.d("MainActivity", "Notification permission granted: $isGranted")
                if (!isGranted) {
                    Toast.makeText(this, "Please grant notification permission for reminders", Toast.LENGTH_LONG).show()
                }
            }
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Request SCHEDULE_EXACT_ALARM permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    Log.d("MainActivity", "Exact alarm permission granted: $isGranted")
                    if (!isGranted) {
                        Toast.makeText(this, "Please grant exact alarm permission for precise reminders", Toast.LENGTH_LONG).show()
                    }
                }
                permissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }
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
