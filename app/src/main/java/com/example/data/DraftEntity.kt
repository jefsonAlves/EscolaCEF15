package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_drafts")
data class DraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "ATTENDANCE", "GRADES", "REPORT"
    val payloadJson: String,
    val status: String = "PENDING", // "PENDING", "SYNCED", "ERROR"
    val timestamp: Long = System.currentTimeMillis()
)
