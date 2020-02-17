package com.ifran.journalapp.persistence

import androidx.room.*
import androidx.room.Junction

@Entity (tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId: Long?,
    var title: String,
    var content: String,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var createdTime: Long?,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var lastModifiedTime: Long?

) {
    constructor(title: String, content: String) : this(null, title, content, null, null) {
        val unixTimeNow = System.currentTimeMillis() / 1000L
        createdTime = unixTimeNow
        lastModifiedTime = unixTimeNow
    }

    fun updateLastModified() {
        lastModifiedTime = System.currentTimeMillis() / 1000L
    }
}

@Entity
data class Tag (
    @PrimaryKey(autoGenerate = true)
    var tagId: Long?,
    var name: String,
    var numNotes: Long

) {
    constructor(name: String) : this( null, name, 0 )

    override fun toString(): String {
        return name
    }
}

@Entity(
    primaryKeys = ["tagId", "noteId"],
    foreignKeys = [
        ForeignKey(entity = Note::class, parentColumns = ["noteId"], childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Tag::class, parentColumns = ["tagId"], childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class NoteTagCrossRef(
    var tagId: Long,
    var noteId: Long
)

data class TagWithNotes (
    @Embedded
    val tag: Tag,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "noteId",
        associateBy = Junction(NoteTagCrossRef::class)
    )
    val notes: List<Note>
)

data class NoteWithTags (
    @Embedded
    val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "tagId",
        associateBy = Junction(NoteTagCrossRef::class)
    )
    val tags: List<Tag>

)