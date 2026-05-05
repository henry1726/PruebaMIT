package com.example.pruebamit.data.repository

import com.example.pruebamit.data.local.dao.UserDao
import com.example.pruebamit.domain.model.User
import com.example.pruebamit.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl@Inject constructor(
    private val userDao: UserDao
) : AuthRepository {
    override suspend fun login(username: String, password: String): User? {
        return if(username == "user12" && password == "admin1"){
            User(username = "userMIT", fullName = "Usuario demo")
        }else{
            null
        }

        //Se crea dao por si la validación se hacía dentro de una db o api
        /*userDao.login(username, password)?.let {
            User(username = it.username, fullName = it.fullName)
        }*/
    }
}