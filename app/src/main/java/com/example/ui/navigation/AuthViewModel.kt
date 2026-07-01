package com.example.ui.navigation

import androidx.lifecycle.ViewModel
import com.example.data.AuthRepository
import com.example.domain.User
import com.example.domain.UserRole
import kotlinx.coroutines.flow.StateFlow

import androidx.lifecycle.viewModelScope
import com.example.data.GoogleAuthService
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository.getInstance()
    private val authService = GoogleAuthService()
    val currentUser: StateFlow<User?> = repository.currentUser

    fun checkAndLoginWithGoogle(email: String, name: String, onUserExists: () -> Unit, onUserNotFound: (String, String) -> Unit) {
        viewModelScope.launch {
            try {
                val existingUser = com.example.data.FirebaseManager.getInstance().getUserByEmail(email)
                if (existingUser != null) {
                    repository.loginExistingUser(existingUser)
                    onUserExists()
                } else {
                    onUserNotFound(email, name)
                }
            } catch (e: Exception) {
                // Network error or something, fallback to sign up
                onUserNotFound(email, name)
            }
        }
    }

    fun login(email: String, role: UserRole, name: String, schoolName: String, sharedViewModel: com.example.ui.viewmodels.SharedViewModel) {
        viewModelScope.launch {
            // Using mock for preview. In a real device use signInWithGoogleToken with ID token
            val result = authService.signInWithGoogleMock(email, role)
            if (result is GoogleAuthService.AuthResult.Success) {
                repository.login(result.email, result.role, name, schoolName, sharedViewModel)
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}
