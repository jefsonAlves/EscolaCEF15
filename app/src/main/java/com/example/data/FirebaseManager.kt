package com.example.data

import com.example.domain.Classroom
import com.example.domain.School
import com.example.domain.Student
import com.example.domain.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseManager private constructor() {
    private val db = FirebaseFirestore.getInstance()

    // References to collections
    private val usersRef = db.collection("users")
    private val schoolsRef = db.collection("schools")
    private val classroomsRef = db.collection("classrooms")
    private val studentsRef = db.collection("students")
    private val draftsRef = db.collection("sync_drafts")

    suspend fun getUserByEmail(email: String): User? {
        val snapshot = usersRef.whereEqualTo("email", email).limit(1).get().await()
        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    // Write operations
    suspend fun saveUser(user: User) {
        usersRef.document(user.id).set(user).await()
    }

    suspend fun saveSchool(school: School) {
        schoolsRef.document(school.id).set(school).await()
    }

    suspend fun saveClassroom(classroom: Classroom) {
        classroomsRef.document(classroom.id).set(classroom).await()
    }

    suspend fun saveStudent(student: Student) {
        studentsRef.document(student.id).set(student).await()
    }

    suspend fun deleteStudent(studentId: String) {
        studentsRef.document(studentId).delete().await()
    }

    suspend fun syncOfflineDrafts(drafts: List<DraftEntity>) {
        val batch = db.batch()
        for (draft in drafts) {
            val docRef = draftsRef.document()
            batch.set(docRef, mapOf(
                "type" to draft.type,
                "payload" to draft.payloadJson,
                "timestamp" to com.google.firebase.Timestamp.now()
            ))
        }
        batch.commit().await()
    }

    // Read streams (Real-time updates)
    fun observeSchools(): Flow<List<School>> = callbackFlow {
        val listener = schoolsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { it.toObject(School::class.java) } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    fun observeClassrooms(schoolId: String? = null): Flow<List<Classroom>> = callbackFlow {
        val query = if (schoolId != null) classroomsRef.whereEqualTo("schoolId", schoolId) else classroomsRef
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { it.toObject(Classroom::class.java) } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    fun observeStudents(classroomId: String? = null): Flow<List<Student>> = callbackFlow {
        val query = if (classroomId != null) studentsRef.whereEqualTo("classroomId", classroomId) else studentsRef
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { it.toObject(Student::class.java) } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    companion object {
        @Volatile
        private var INSTANCE: FirebaseManager? = null

        fun getInstance(): FirebaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseManager().also { INSTANCE = it }
            }
        }
    }
}
