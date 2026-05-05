package com.example.pruebamit.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pruebamit.data.local.dao.CardDao
import com.example.pruebamit.data.local.dao.MovementDao
import com.example.pruebamit.data.local.dao.UserDao
import com.example.pruebamit.data.local.entities.CardEntity
import com.example.pruebamit.data.local.entities.MovementEntity
import com.example.pruebamit.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, CardEntity::class, MovementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun cardDao(): CardDao
    abstract fun movementDao(): MovementDao

    companion object {
        const val DATABASE_NAME = "mibanca_db"
    }
}