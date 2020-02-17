package com.ifran.journalapp.persistence

import android.content.Context
import androidx.core.graphics.scaleMatrix
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.sql.Timestamp

@Database(entities = [Note::class, Tag::class, NoteTagCrossRef::class], exportSchema = false, version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao

    companion object {

        @Volatile private var INSTANCE: NotesDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): NotesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, scope).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context, scope: CoroutineScope) =
            Room.databaseBuilder(context.applicationContext,
                NotesDatabase::class.java, "Notes.db")
                //.addCallback(NoteDatabaseCallback(scope))
                //.fallbackToDestructiveMigration()
                .build()
    }


    private class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val noteDao = database.noteDao()
                    val tagDao = database.tagDao()

                    // Delete all content here.
                    //noteDao.deleteAllNotes()
                    var note: Note
//                    val timestamp = Timestamp(System.currentTimeMillis()).toString()
//                    var note = Note( "hello world", "Text Content")
//                    noteDao.insertNote(note)
//
//                    note = Note( "This App..", "Might be good")
//                    noteDao.insertNote(note)
//
//                    note = Note( "Hmm..", "Must finish this today")
//                    noteDao.insertNote(note)
//
//                    val tag = Tag("DANK")
//                    tagDao.insertTag(tag)
//
//                    val ntc = NoteTagCrossRef(1, 1)
//                    tagDao.insertNoteWithTag(ntc)
                    tagDao.insertNoteWithTag(NoteTagCrossRef(2, 2))
                    tagDao.insertNoteWithTag(NoteTagCrossRef(2, 3))
                    tagDao.insertNoteWithTag(NoteTagCrossRef(2, 4))
                    tagDao.insertNoteWithTag(NoteTagCrossRef(2, 5))
                    tagDao.insertNoteWithTag(NoteTagCrossRef(2, 7))
                    tagDao.insertNoteWithTag(NoteTagCrossRef(2, 8))

                    tagDao.insertNoteWithTag(NoteTagCrossRef(1, 8))
                    tagDao.insertNoteWithTag(NoteTagCrossRef(1, 5))

                }
            }
        }
    }
}