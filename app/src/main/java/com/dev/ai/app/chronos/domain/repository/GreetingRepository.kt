package com.dev.ai.app.chronos.domain.repository

interface GreetingRepository {
    suspend fun getAIGreeting(prompt: String): String
}