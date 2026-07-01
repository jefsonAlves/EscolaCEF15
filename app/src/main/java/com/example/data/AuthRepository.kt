package com.example.data

import com.example.domain.User
import com.example.domain.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthRepository private constructor() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun loginExistingUser(user: User) {
        _currentUser.value = user
    }

    fun login(email: String, role: UserRole, name: String, schoolName: String, sharedViewModel: com.example.ui.viewmodels.SharedViewModel) {
        val assignedRole = if (email == "jefson.s.a7@gmail.com") UserRole.SUPER_ADMIN else role
        
        val user = User(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            email = email,
            role = assignedRole
        )
        
        _currentUser.value = user
        
        // Save to Firebase (via SharedViewModel or directly to FirebaseManager)
        kotlinx.coroutines.GlobalScope.launch {
            com.example.data.FirebaseManager.getInstance().saveUser(user)
        }

        // If it's a School admin, create the school
        if (assignedRole == UserRole.ADMIN && schoolName.isNotBlank()) {
            val school = com.example.domain.School(
                id = java.util.UUID.randomUUID().toString(),
                name = schoolName,
                address = "Adicione endereço",
                code = schoolName.take(3).uppercase()
            )
            sharedViewModel.addSchool(school)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository().also { INSTANCE = it }
            }
        }
    }
}
