package com.example.pruebamit.presentation.movements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebamit.domain.model.Movement
import com.example.pruebamit.domain.usecase.GetMovementsUseCase
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
class MovementsViewModel @Inject constructor(
    private val getMovementsUseCase: GetMovementsUseCase
) : ViewModel(){
    private val _movements = MutableStateFlow<Resource<List<Movement>>>(Resource.Idle)
    val movements: StateFlow<Resource<List<Movement>>> = _movements.asStateFlow()

    init {
        loadMovements()
    }

    private fun loadMovements() {
        viewModelScope.launch {
            getMovementsUseCase()
                .onStart { _movements.value = Resource.Loading }
                .catch { e ->
                    _movements.value = Resource.Error("Error al cargar movimientos: ${e.message}", e)
                }
                .collect { movementList ->
                    _movements.value = Resource.Success(movementList)
                }
        }
    }
}