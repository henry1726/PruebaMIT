package com.example.pruebamit.domain.model

data class Card(
    val id: Int = 0,
    val cardholderName: String,
    val cardNumber: String,
    val expirationDate: String
){
    fun getFormattedCardNumber(): String {
        return "**** **** **** ${cardNumber.takeLast(4)}"
    }
}
