package com.dev.ai.app.chronos.di

import android.content.Context
import com.dev.ai.app.chronos.data.repository.GreetingRepositoryImpl
import com.dev.ai.app.chronos.data.repository.ReminderRepositoryImpl
import com.dev.ai.app.chronos.domain.repository.GreetingRepository
import com.dev.ai.app.chronos.domain.repository.ReminderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideReminderRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): ReminderRepository {
        return ReminderRepositoryImpl(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO)
    }

    @Provides
    @Singleton
    fun provideGreetingRepository(client: HttpClient): GreetingRepository {
        return GreetingRepositoryImpl(client)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

}