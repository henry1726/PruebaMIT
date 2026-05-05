package com.example.pruebamit.domain.usecase

import com.example.pruebamit.domain.model.User
import com.example.pruebamit.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): User? {
        if (username.length != 6 || password.length < 6) {
            return null
        }
        return authRepository.login(username, password)
    }
}