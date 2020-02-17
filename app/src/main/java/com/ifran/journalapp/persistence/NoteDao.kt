package com.ifran.journalapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface NoteDao {

    @Query("SELECT * FROM Notes order by lastModifiedTime desc")
    fun getAll(): LiveData<List<Note>>

    @Query("select * from notes where noteId in (select noteId from NoteTagCrossRef where tagId == :tagId) order by lastModifiedTime desc")
    suspend fun getAllNotesByTag2(tagId: Long): List<Note>

    @Query("SELECT * FROM Notes where noteId in (select noteId from NoteTagCrossRef where tagId == :tagId) order by lastModifiedTime desc")
    fun getAllNotesByTag(tagId: Long): LiveData<List<Note>>

    @Query("select * from Notes where noteId == :id")
    fun getNoteById (id: String): LiveData<Note>

    @Query("SELECT * FROM Notes WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): LiveData<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert
    suspend fun insertAll(vararg notes: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(notes: List<Note>)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM Notes")
    suspend fun deleteAllNotes()

    @Query("delete from Notes where noteId in (:noteIds)")
    suspend fun deleteNotesByIds(noteIds: List<Long>)
}