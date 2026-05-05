package com.example.pruebamit.di

import com.example.pruebamit.data.repository.AuthRepositoryImpl
import com.example.pruebamit.data.repository.CardRepositoryImpl
import com.example.pruebamit.data.repository.MovementRepositoryImpl
import com.example.pruebamit.domain.repository.AuthRepository
import com.example.pruebamit.domain.repository.CardRepository
import com.example.pruebamit.domain.repository.MovementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(impl: CardRepositoryImpl): CardRepository

    @Binds
    @Singleton
    abstract fun bindMovementRepository(impl: MovementRepositoryImpl): MovementRepository
}