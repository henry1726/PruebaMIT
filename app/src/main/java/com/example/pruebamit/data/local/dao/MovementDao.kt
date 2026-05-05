package com.example.pruebamit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pruebamit.data.local.entities.MovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {
    @Query("SELECT * FROM movements ORDER BY id DESC")
    fun getAllMovements(): Flow<List<MovementEntity>>

    @Insert
    suspend fun insertMovement(movement: MovementEntity)
}