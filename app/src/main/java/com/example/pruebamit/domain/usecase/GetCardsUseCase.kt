package com.example.pruebamit.domain.usecase

import com.example.pruebamit.domain.model.Card
import com.example.pruebamit.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardsUseCase @Inject constructor(
    private val cardRepository: CardRepository
){
    operator fun invoke(): Flow<List<Card>> {
        return cardRepository.getCards()
    }
}