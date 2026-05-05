package com.example.pruebamit.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebamit.data.local.prefs.SessionManager
import com.example.pruebamit.domain.model.User
import com.example.pruebamit.domain.usecase.LoginUseCase
import com.example.pruebamit.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel(){

    private val _loginState = MutableStateFlow<Resource<User>>(Resource.Idle)
    val loginState: StateFlow<Resource<User>> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            try {
                val user = loginUseCase(username, password)
                _loginState.value = if (user != null) {
                    sessionManager.saveSession(user.username)
                    Resource.Success(user)
                } else {
                    Resource.Error("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Error al iniciar sesión: ${e.message}", e)
            }
        }
    }

    fun isUserLoggedIn() = sessionManager.isLoggedIn()

    fun resetState() {
        _loginState.value = Resource.Idle
    }
}