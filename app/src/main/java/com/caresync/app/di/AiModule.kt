package com.caresync.app.di

import com.caresync.app.ai.OfflineAiService
import com.caresync.app.ai.OfflineMedicalAiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindOfflineAiService(
        impl: OfflineMedicalAiServiceImpl
    ): OfflineAiService
}
