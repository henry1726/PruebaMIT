package com.example.pruebamit.domain.usecase

import com.example.pruebamit.domain.model.Card
import com.example.pruebamit.domain.repository.CardRepository
import javax.inject.Inject

class AddCardUseCase @Inject constructor(
    private val cardRepository: CardRepository
){
    suspend operator fun invoke(card: Card) {
        if (card.cardNumber.length != 19) {
            throw IllegalArgumentException("Número de tarjeta inválido")
        }
        if (card.cardholderName.isBlank()) {
            throw IllegalArgumentException("Nombre del titular requerido")
        }

        cardRepository.addCard(card)
    }
}