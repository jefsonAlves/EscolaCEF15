package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseManager
import com.example.domain.Classroom
import com.example.domain.School
import com.example.domain.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()

    val schools: StateFlow<List<School>> = firebaseManager.observeSchools()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val classrooms: StateFlow<List<Classroom>> = firebaseManager.observeClassrooms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val students: StateFlow<List<Student>> = firebaseManager.observeStudents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Preparado para receber novos dados
    fun addSchool(school: School) {
        viewModelScope.launch {
            try {
                firebaseManager.saveSchool(school)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addClassroom(classroom: Classroom) {
        viewModelScope.launch {
            try {
                firebaseManager.saveClassroom(classroom)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            try {
                firebaseManager.saveStudent(student)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
