package com.dev.ai.app.chronos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChronosApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}