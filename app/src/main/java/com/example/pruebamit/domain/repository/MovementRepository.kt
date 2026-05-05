package com.example.pruebamit.domain.repository

import com.example.pruebamit.domain.model.Movement
import kotlinx.coroutines.flow.Flow

interface MovementRepository {
    fun getMovements(): Flow<List<Movement>>
    suspend fun addMovement(movement: Movement)
}