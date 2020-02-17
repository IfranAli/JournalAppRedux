package com.ifran.journalapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.ifran.journalapp.persistence.*

class NoteRepository( private val noteDao: NoteDao) {
    val allNotes = noteDao.getAll()
    val notesbytag = noteDao.getAllNotesByTag(2)

    suspend fun test(): List<Note> {
        val noteswithtags = noteDao.getAllNotesByTag(1)

        val notesbytag2 = noteDao.getAllNotesByTag(2)
        val all = noteDao.getAll()

        return emptyList()
    }

    suspend fun insert(note: Note): Long {
        return noteDao.insertNote(note)
    }

    fun findById(id: Long): LiveData<Note> {
       return noteDao.getNoteById(id.toString())
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNotesByIds(noteIds: List<Long>) {
        noteDao.deleteNotesByIds(noteIds)
    }

    fun getAllNotesByTag(tag: Tag) {
//        tag.tagId?.let {
//            allNotes = noteDao.getAllNotesByTag()
//        }
    }

    fun getAllNotes() {
//        allNotes = noteDao.getAll()
    }
}