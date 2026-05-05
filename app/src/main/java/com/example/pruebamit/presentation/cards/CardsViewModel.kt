package com.example.pruebamit.presentation.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebamit.domain.model.Card
import com.example.pruebamit.domain.usecase.AddCardUseCase
import com.example.pruebamit.domain.usecase.GetCardsUseCase
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
class CardsViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase
) : ViewModel(){
    private val _cards = MutableStateFlow<Resource<List<Card>>>(Resource.Idle)
    val cards: StateFlow<Resource<List<Card>>> = _cards.asStateFlow()

    private val _addCardState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val addCardState: StateFlow<Resource<Unit>> = _addCardState.asStateFlow()

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

    fun addCard(cardholderName: String, cardNumber: String, expirationDate: String) {
        viewModelScope.launch {
            _addCardState.value = Resource.Loading
            try {
                val card = Card(
                    cardholderName = cardholderName,
                    cardNumber = cardNumber,
                    expirationDate = expirationDate
                )
                addCardUseCase(card)
                _addCardState.value = Resource.Success(Unit)
            } catch (e: IllegalArgumentException) {
                _addCardState.value = Resource.Error(e.message ?: "Error de validación", e)
            } catch (e: Exception) {
                _addCardState.value = Resource.Error("Error al agregar tarjeta: ${e.message}", e)
            }
        }
    }

    fun resetAddCardState() {
        _addCardState.value = Resource.Idle
    }
}