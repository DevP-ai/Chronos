package com.dev.ai.app.chronos.data.uistate

sealed class GreetingState {
    object Idle : GreetingState()
    object Loading : GreetingState()
    data class Success(val message: String) : GreetingState()
    data class Error(val message: String) : GreetingState()
}