package com.dev.ai.app.chronos.data.usecase

import com.dev.ai.app.chronos.domain.repository.GreetingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAIGreetingUseCase @Inject constructor(
    private val greetingRepository: GreetingRepository
) {
    suspend operator fun invoke(prompt: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val greeting = greetingRepository.getAIGreeting(prompt)
                Result.success(greeting)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}