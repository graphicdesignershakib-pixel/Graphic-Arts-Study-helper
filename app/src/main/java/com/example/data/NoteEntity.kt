package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val summary: String,
    val content: String,
    val category: String, // e.g. "LAYOUT", "COLOR", "TYPE", "TRENDS"
    val imageUrl: String,
    val masteryLevel: Int, // e.g. 84
    val examRelevance: Double, // e.g. 9.2
    val tags: String, // Comma separated, e.g. "swiss-design,grid-systems"
    val timestampText: String, // e.g. "2h ago"
    val isMastered: Boolean = false,
    val editHistory: String // Pipe or comma-separated edit events, edit date/time
)
