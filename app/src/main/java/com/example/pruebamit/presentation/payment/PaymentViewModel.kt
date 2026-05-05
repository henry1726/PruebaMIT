package com.example.pruebamit.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebamit.domain.model.Card
import com.example.pruebamit.domain.usecase.GetCardsUseCase
import com.example.pruebamit.domain.usecase.MakePaymentUseCase
import com.example.pruebamit.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
    private val makePaymentUseCase: MakePaymentUseCase
) : ViewModel(){
    private val _cards = MutableStateFlow<Resource<List<Card>>>(Resource.Idle)
    val cards: StateFlow<Resource<List<Card>>> = _cards.asStateFlow()

    private val _paymentState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val paymentState: StateFlow<Resource<Unit>> = _paymentState.asStateFlow()

    init {
        loadCards()
    }

    fun loadCards() {
        viewModelScope.launch {
            getCardsUseCase()
                .onStart { _cards.value = Resource.Loading }
                .catch { e ->
                    _cards.value = Resource.Error("Error al cargar tarjetas: ${e.message}", e)
                }
                .collect { cardList ->
                    _cards.value = Resource.Success(cardList)
                }
        }
    }

    fun makePayment(
        sourceCardLast4: String,
        destinationCard: String,
        recipientName: String,
        reason: String,
        amount: Double,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _paymentState.value = Resource.Loading
            try {

                val cardsResource = _cards.value
                if (cardsResource !is Resource.Success) {
                    _paymentState.value = Resource.Error("No hay tarjetas disponibles")
                    return@launch
                }

                val sourceCard = cardsResource.data.firstOrNull {
                    it.cardNumber.endsWith(sourceCardLast4.replace("*", "").trim())
                }

                if (sourceCard == null) {
                    _paymentState.value = Resource.Error("Tarjeta de origen no encontrada")
                    return@launch
                }

                makePaymentUseCase(
                    sourceCardId = sourceCard.id,
                    destinationCard = destinationCard,
                    recipientName = recipientName,
                    reason = reason,
                    amount = amount,
                    latitude = latitude,
                    longitude = longitude
                )
                _paymentState.value = Resource.Success(Unit)
            } catch (e: IllegalArgumentException) {
                _paymentState.value = Resource.Error(e.message ?: "Error de validación", e)
            } catch (e: Exception) {
                _paymentState.value = Resource.Error("Error al realizar pago: ${e.message}", e)
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = Resource.Idle
    }
}