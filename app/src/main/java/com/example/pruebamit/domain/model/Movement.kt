package com.example.pruebamit.domain.model

data class Movement(
    val id: Int = 0,
    val sourceCardId: Int,
    val destinationCard: String,
    val recipientName: String,
    val reason: String,
    val amount: Double,
    val date: String,
    val time: String,
    val location: String
){
    fun getFormattedAmount(): String {
        return "-$${"%.2f".format(amount)}"
    }

    fun getDateTime(): String {
        return "$date - $time"
    }

}