package com.example.data

import android.content.Context
import com.example.domain.UserRole
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Service to handle Firebase Authentication and Firestore logic.
 */
class GoogleAuthService {
    private val auth = FirebaseAuth.getInstance()

    /**
     * Completes the sign-in flow after receiving a Google ID token.
     * Use CredentialManager in your UI to get the token, then pass it here.
     */
    suspend fun signInWithGoogleToken(idToken: String, requestedRole: UserRole): AuthResult {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user

            if (user != null) {
                val email = user.email ?: ""
                
                // Detect the master admin email and override the role
                val finalRole = if (email.equals("jefson.s.a7@gmail.com", ignoreCase = true)) {
                    UserRole.SUPER_ADMIN
                } else {
                    requestedRole
                }

                AuthResult.Success(email, finalRole)
            } else {
                AuthResult.Error("Falha na autenticação do Firebase.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Erro desconhecido na autenticação.")
        }
    }

    // Mock sign in for preview/development when Firebase is not fully configured
    suspend fun signInWithGoogleMock(email: String, requestedRole: UserRole): AuthResult {
        kotlinx.coroutines.delay(1000)
        val finalRole = if (email.equals("jefson.s.a7@gmail.com", ignoreCase = true)) {
            UserRole.SUPER_ADMIN
        } else {
            requestedRole
        }
        return AuthResult.Success(email, finalRole)
    }

    sealed class AuthResult {
        data class Success(val email: String, val role: UserRole) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
