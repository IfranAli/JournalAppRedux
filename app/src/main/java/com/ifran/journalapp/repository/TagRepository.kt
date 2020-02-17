package com.ifran.journalapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.ifran.journalapp.persistence.*
import kotlinx.coroutines.launch

class TagRepository(private val tagDao: TagDao) {
    
    val allTags = tagDao.getAllTags()

    suspend fun insertNoteWithTag(noteTagCrossRef: NoteTagCrossRef): Long {
        return tagDao.insertNoteWithTag(noteTagCrossRef)
    }

    suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag)
    }

    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(tag)
    }

    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag)
    }

    suspend fun insertTagsForNote(noteTagCrossRefs: List<NoteTagCrossRef>) {
        tagDao.insertTagsForNote(*noteTagCrossRefs.toTypedArray())
    }

    suspend fun deleteTagsForNote(noteTagCrossRefs: List<NoteTagCrossRef>) {
        tagDao.deleteTagsForNote(*noteTagCrossRefs.toTypedArray())
    }

    fun getTagsWithNotes(): LiveData<List<TagWithNotes>> {
        return tagDao.getTagsWithNotes()
    }

    fun getNotesWithTags(): LiveData<List<NoteWithTags>> {
        return tagDao.getNotesWithTags()
    }

    suspend fun deleteAllTags() {
        tagDao.deleteAllTags()
    }

}