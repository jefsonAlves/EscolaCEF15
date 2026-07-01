package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {
    @Query("SELECT * FROM offline_drafts ORDER BY timestamp DESC")
    fun getAllDrafts(): Flow<List<DraftEntity>>

    @Query("SELECT * FROM offline_drafts WHERE status = :status ORDER BY timestamp DESC")
    fun getDraftsByStatus(status: String): Flow<List<DraftEntity>>

    @Query("SELECT * FROM offline_drafts WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getDraftsByStatusOnce(status: String): List<DraftEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftEntity)

    @androidx.room.Update
    suspend fun updateDrafts(drafts: List<DraftEntity>)

    @Query("DELETE FROM offline_drafts WHERE id = :id")
    suspend fun deleteDraftById(id: Int)
}
