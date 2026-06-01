package com.example.data

import kotlinx.coroutines.flow.Flow

class StudyRepository(private val studyDao: StudyDao) {
    val allNotes: Flow<List<NoteEntity>> = studyDao.getAllNotes()
    val allChatMessages: Flow<List<ChatMessageEntity>> = studyDao.getChatMessages()

    suspend fun getNoteById(id: Int): NoteEntity? {
        return studyDao.getNoteById(id)
    }

    suspend fun insertNote(note: NoteEntity) {
        studyDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        studyDao.updateNote(note)
    }

    suspend fun insertNotes(notes: List<NoteEntity>) {
        studyDao.insertNotes(notes)
    }

    suspend fun insertChatMessage(message: ChatMessageEntity) {
        studyDao.insertChatMessage(message)
    }

    suspend fun clearChatHistory() {
        studyDao.clearChatHistory()
    }
}
