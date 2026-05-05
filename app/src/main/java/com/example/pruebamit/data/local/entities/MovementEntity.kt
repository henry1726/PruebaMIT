package com.example.pruebamit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceCardId: Int,
    val destinationCard: String,
    val recipientName: String,
    val reason: String,
    val amount: Double,
    val date: String,
    val time: String,
    val location: String
)
