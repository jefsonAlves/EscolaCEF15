package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineSyncManager private constructor(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "colegio_em_movimento_db"
    ).build()

    private val draftDao = database.draftDao()

    val pendingDraftsCount: Flow<Int> = draftDao.getDraftsByStatus("PENDING").map { it.size }

    suspend fun saveDraft(type: String, payload: String) {
        draftDao.insertDraft(
            DraftEntity(
                type = type,
                payloadJson = payload,
                status = "PENDING"
            )
        )
    }

    suspend fun syncDrafts() {
        try {
            // Get all pending drafts
            val pendingDrafts = draftDao.getDraftsByStatusOnce("PENDING")
            if (pendingDrafts.isNotEmpty()) {
                // Sync with Firestore
                val firebaseManager = FirebaseManager.getInstance()
                firebaseManager.syncOfflineDrafts(pendingDrafts)

                // Update status to SYNCED locally
                val syncedDrafts = pendingDrafts.map { it.copy(status = "SYNCED") }
                draftDao.updateDrafts(syncedDrafts)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Sync failed, drafts remain PENDING
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: OfflineSyncManager? = null

        fun getInstance(context: Context): OfflineSyncManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: OfflineSyncManager(context).also { INSTANCE = it }
            }
        }
    }
}
