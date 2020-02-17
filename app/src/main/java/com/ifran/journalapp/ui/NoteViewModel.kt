package com.ifran.journalapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ifran.journalapp.persistence.*
import com.ifran.journalapp.repository.NoteRepository
import com.ifran.journalapp.repository.TagRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val tagRepository: TagRepository
    private val noteRepository: NoteRepository

    private val dBNotesWithTags: LiveData<List<NoteWithTags>>
    
    val allTags: LiveData<List<Tag>>
    val allNotesWithTags = MediatorLiveData<List<NoteWithTags>>()

    private var filterTags = emptyList<Tag>().toMutableList()
    var selectedNote = MutableLiveData<NoteWithTags>()

    init {
        val noteDao = NotesDatabase.getInstance(application, viewModelScope).noteDao()
        val tagDao = NotesDatabase.getInstance(application, viewModelScope).tagDao()
        noteRepository = NoteRepository(noteDao)
        tagRepository = TagRepository(tagDao)
        dBNotesWithTags = tagRepository.getNotesWithTags()
        allTags = tagRepository.allTags

        allNotesWithTags.addSource(dBNotesWithTags) {
            applyTagsFilter()
        }
    }

    private fun applyTagsFilter() {
        //for (tag in tags) Log.e("noteviewmodel", "Applying filter: ${tag.name}")

        dBNotesWithTags.value?.let {
            allNotesWithTags.value =
                when(filterTags.isEmpty()) {
                    true -> it
                    false -> it.filter {
                            noteWithTags -> noteWithTags.tags.any(filterTags::contains)
                    }
                }
        }
    }

    fun setTagsFilter( tags: List<Tag> ) {
        filterTags = tags.toMutableList()
        applyTagsFilter()
    }
    fun setTagsFilterNoTagsAssigned () {
        dBNotesWithTags.value?.let {
            allNotesWithTags.value =
                it.filter {
                        noteWithTags -> noteWithTags.tags.isEmpty()
                }
        }
    }

    fun insert( note: Note ) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    // TODO: Clean up
    fun insertSync( note: Note ) = viewModelScope.launch {
        val noteId =  noteRepository.insert(note)
        selectedNote.value?.note?.noteId = noteId
    }

    fun updateNote(note: Note) = viewModelScope.launch{
        noteRepository.updateNote(note)
    }

    fun insertNoteWithTags( noteWithTags: NoteWithTags ) = viewModelScope.launch {
        noteWithTags.note.noteId = noteRepository.insert(noteWithTags.note)
        insertTagsForNote(noteWithTags.note, noteWithTags.tags)
    }

    fun setSelectedNoteById(noteId: Long) {
        val note = dBNotesWithTags.value?.find {
                noteWithTags -> noteWithTags.note.noteId == noteId
        }
        selectedNote.value = note
    }

    fun setSelectedNote(position: Int) {
        val note = dBNotesWithTags.value?.get(position)
        selectedNote.value = note
    }

    fun deleteNotesByIds(noteIds: List<Long>) = viewModelScope.launch{
        noteRepository.deleteNotesByIds(noteIds)
    }

    fun insertTag( name: String ) = viewModelScope.launch {
        tagRepository.insertTag(Tag(name))
    }

    fun deleteAllTags() = viewModelScope.launch {
        tagRepository.deleteAllTags()
    }

    fun insertTagsForNote( note: Note, tags: List<Tag>) = viewModelScope.launch {
        val noteTagCrossRefs = mutableListOf<NoteTagCrossRef>()
        tags.forEach {
            noteTagCrossRefs.add(NoteTagCrossRef(it.tagId!!, note.noteId!!))
        }
        tagRepository.insertTagsForNote(noteTagCrossRefs)
    }

    fun deleteTagsForNote( note: Note, tags: List<Tag>) = viewModelScope.launch {
        val noteTagCrossRefs = mutableListOf<NoteTagCrossRef>()
        tags.forEach {
            noteTagCrossRefs.add(NoteTagCrossRef(it.tagId!!, note.noteId!!))
        }
        tagRepository.deleteTagsForNote(noteTagCrossRefs)
    }
}
