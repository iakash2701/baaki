package com.baaki.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.repository.AuthRepository
import com.baaki.app.data.repository.UserSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    val userSession: StateFlow<UserSession> = authRepository.userSessionFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSession("rajesh@sharmapackaging.in", "Rajesh Sharma", "Sharma Packaging", "9876543210", true)
    )

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.login(email, pass)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Login failed"
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, pass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (pass != confirmPass) {
            _errorMessage.value = "Passwords do not match."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.register(fullName, email, phone, pass)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Registration failed"
            }
        }
    }

    fun updateProfile(fullName: String, businessName: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.updateProfile(fullName, businessName, phone)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Profile update failed"
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }

    fun socialLogin(provider: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val email = "user@${provider.lowercase()}.com"
            val name = "Demo User ($provider)"
            authRepository.register(name, email, "9876543210", "oauth123")
            _isLoading.value = false
            onSuccess()
        }
    }
}
