package com.example.pruebamit.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebamit.data.local.prefs.SessionManager
import com.example.pruebamit.domain.model.ProfileData
import com.example.pruebamit.domain.usecase.GetProfileDataUseCase
import com.example.pruebamit.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileDataUseCase: GetProfileDataUseCase,
    private val sessionManager: SessionManager
) : ViewModel(){
    private val _profileData = MutableStateFlow<Resource<ProfileData>>(Resource.Idle)
    val profileData: StateFlow<Resource<ProfileData>> = _profileData.asStateFlow()

    fun loadProfileData() {
        val username = sessionManager.getUsername() ?: "Invitado"
        viewModelScope.launch {
            _profileData.value = Resource.Loading
            try {
                val data = getProfileDataUseCase(username)
                _profileData.value = Resource.Success(data)
            } catch (e: Exception) {
                _profileData.value = Resource.Error("Error al cargar perfil: ${e.message}", e)
            }
        }
    }

    fun logout() {
        sessionManager.logout()
    }
}