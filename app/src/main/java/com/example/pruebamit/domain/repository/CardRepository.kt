package com.example.pruebamit.domain.repository

import com.example.pruebamit.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getCards(): Flow<List<Card>>
    suspend fun addCard(card: Card)
}