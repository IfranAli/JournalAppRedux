package com.ifran.journalapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TagDao {

    /* Transactions on cross reference table */
    @Transaction
    @Query("SELECT * FROM Tag")
    fun getTagsWithNotes(): LiveData<List<TagWithNotes>>

    @Transaction
    @Query("SELECT * FROM Notes order by lastModifiedTime desc")
    fun getNotesWithTags(): LiveData<List<NoteWithTags>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteWithTag(noteTagCrossRef: NoteTagCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagsForNote(vararg noteTagCrossRefs: NoteTagCrossRef)

    @Delete
    suspend fun deleteTagsForNote(vararg noteTagCrossRefs: NoteTagCrossRef)

    /* Transactions on Tag table */
    @Query("SELECT * FROM Tag order by name desc")
    fun getAllTags(): LiveData<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Insert
    suspend fun insertAllTags(vararg tags: Tag)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("DELETE FROM Tag")
    suspend fun deleteAllTags()
}