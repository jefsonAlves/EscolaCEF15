package com.example.domain

enum class UserRole {
    SUPER_ADMIN, ADMIN, TEACHER, STUDENT, PARENT
}

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, JUSTIFIED
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT,
    val photoUrl: String? = null
)

data class School(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val code: String = ""
)

data class SchoolMember(
    val id: String = "",
    val schoolId: String = "",
    val userId: String = "",
    val role: UserRole = UserRole.STUDENT
)

data class Classroom(
    val id: String = "",
    val schoolId: String = "",
    val name: String = "",
    val level: String = "",
    val shift: String = "",
    val year: Int = 0
)

data class Student(
    val id: String = "",
    val classroomId: String = "",
    val name: String = "",
    val registrationNumber: String = "",
    val dateOfBirth: String? = null,
    val grade: String = "",
    val parentContact: String = ""
)
