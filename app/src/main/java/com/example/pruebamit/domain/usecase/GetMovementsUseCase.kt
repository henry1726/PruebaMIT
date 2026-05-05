package com.example.pruebamit.domain.usecase

import com.example.pruebamit.domain.model.Movement
import com.example.pruebamit.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovementsUseCase @Inject constructor(
    private val movementRepository: MovementRepository
){
    operator fun invoke(): Flow<List<Movement>> {
        return movementRepository.getMovements()
    }
}