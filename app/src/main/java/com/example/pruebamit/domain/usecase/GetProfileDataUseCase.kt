package com.example.pruebamit.domain.usecase

import com.example.pruebamit.domain.model.ProfileData
import com.example.pruebamit.domain.repository.CardRepository
import com.example.pruebamit.domain.repository.MovementRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetProfileDataUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val movementRepository: MovementRepository
){
    suspend operator fun invoke(username: String): ProfileData {
        val totalCards = cardRepository.getCards().first().size
        val totalMovements = movementRepository.getMovements().first().size

        return ProfileData(
            username = username,
            totalCards = totalCards,
            totalMovements = totalMovements
        )
    }
}