package com.example.pruebamit.domain.usecase

import com.example.pruebamit.domain.model.Movement
import com.example.pruebamit.domain.repository.MovementRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class MakePaymentUseCase @Inject constructor(
    private val movementRepository: MovementRepository
){
    suspend operator fun invoke(
        sourceCardId: Int,
        destinationCard: String,
        recipientName: String,
        reason: String,
        amount: Double,
        latitude: Double,
        longitude: Double
    ) {
        if (amount <= 0) {
            throw IllegalArgumentException("El monto debe ser mayor a 0")
        }
        if (destinationCard.length != 16) {
            throw IllegalArgumentException("Número de tarjeta destinatario inválido")
        }

        val currentTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val movement = Movement(
            sourceCardId = sourceCardId,
            destinationCard = destinationCard,
            recipientName = recipientName,
            reason = reason,
            amount = amount,
            date = dateFormat.format(currentTime.time),
            time = timeFormat.format(currentTime.time),
            location = "Lat: ${"%.4f".format(latitude)}, Lon: ${"%.4f".format(longitude)}"
        )

        movementRepository.addMovement(movement)
    }
}