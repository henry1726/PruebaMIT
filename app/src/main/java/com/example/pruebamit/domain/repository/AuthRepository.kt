package com.example.pruebamit.domain.repository

import com.example.pruebamit.domain.model.User


interface AuthRepository {
    suspend fun login(username: String, password: String): User?
}