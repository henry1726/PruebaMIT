package com.example.pruebamit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardholderName: String,
    val cardNumber: String,
    val expirationDate: String
)
