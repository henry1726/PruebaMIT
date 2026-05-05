package com.example.pruebamit.data.repository

import com.example.pruebamit.data.local.dao.CardDao
import com.example.pruebamit.data.local.entities.CardEntity
import com.example.pruebamit.domain.model.Card
import com.example.pruebamit.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {
    override fun getCards(): Flow<List<Card>> =
        cardDao.getAllCards().map { list ->
            list.map { Card(it.id, it.cardholderName, it.cardNumber, it.expirationDate) }
        }

    override suspend fun addCard(card: Card) {
        cardDao.insertCard(
            CardEntity(
                cardholderName = card.cardholderName,
                cardNumber = card.cardNumber,
                expirationDate = card.expirationDate
            )
        )
    }
}