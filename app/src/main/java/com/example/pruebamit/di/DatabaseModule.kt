package com.example.pruebamit.di

import android.content.Context
import androidx.room.Room
import com.example.pruebamit.data.local.dao.CardDao
import com.example.pruebamit.data.local.dao.MovementDao
import com.example.pruebamit.data.local.dao.UserDao
import com.example.pruebamit.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()
    @Provides fun provideMovementDao(db: AppDatabase): MovementDao = db.movementDao()
}