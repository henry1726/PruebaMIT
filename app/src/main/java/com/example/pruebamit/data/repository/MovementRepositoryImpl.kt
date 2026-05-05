package com.example.pruebamit.data.repository

import com.example.pruebamit.data.local.dao.MovementDao
import com.example.pruebamit.data.local.entities.MovementEntity
import com.example.pruebamit.domain.model.Movement
import com.example.pruebamit.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovementRepositoryImpl @Inject constructor(
    private val movementDao: MovementDao
) : MovementRepository{

    override fun getMovements(): Flow<List<Movement>> =
        movementDao.getAllMovements().map { list ->
            list.map { Movement(
                sourceCardId = it.sourceCardId,
                destinationCard = it.destinationCard,
                recipientName = it.recipientName,
                reason = it.reason,
                amount = it.amount,
                date = it.date,
                time = it.time,
                location = it.location
            ) }
        }


    override suspend fun addMovement(movement: Movement) {
        movementDao.insertMovement(
            MovementEntity(
                sourceCardId = movement.sourceCardId,
                destinationCard = movement.destinationCard,
                recipientName = movement.recipientName,
                reason = movement.reason,
                amount = movement.amount,
                date = movement.date,
                time = movement.time,
                location = movement.location
            )
        )
    }
}