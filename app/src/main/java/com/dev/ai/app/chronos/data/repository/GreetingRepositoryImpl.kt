package com.dev.ai.app.chronos.data.repository

import com.dev.ai.app.chronos.domain.repository.GreetingRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class GreetingRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : GreetingRepository {
    override suspend fun getAIGreeting(prompt: String): String {
        return client.get("https://text.pollinations.ai/prompt/$prompt").bodyAsText()
    }
}