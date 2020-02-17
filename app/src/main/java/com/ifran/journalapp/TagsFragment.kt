package com.ifran.journalapp


import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ifran.journalapp.persistence.NoteWithTags
import com.ifran.journalapp.persistence.Tag
import com.ifran.journalapp.ui.NoteViewModel
import kotlinx.android.synthetic.main.fragment_tags.view.*

class TagsFragment : DialogFragment() {

    private val noteViewModel: NoteViewModel by activityViewModels()

    private lateinit var tags: MutableList<Tag>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tags, container, false).run {

            val note = noteViewModel.selectedNote.value!!

            val newTagEditText = tag_dialog_editText_Tag
            val checkboxContainer = tag_dialog_linearlayout

            tag_dialog_button_Add.setOnClickListener {
                val newTagName = newTagEditText.text
                if (newTagName.isNotEmpty()) {
                    noteViewModel.insertTag(newTagName.toString())
                    newTagEditText.setText("")
                }
            }

            // TODO: Logic can be moved to viewmodel
            tag_dialog_button_SaveChanges.setOnClickListener {
                val t1 = mutableSetOf<Tag>()
                val t2 = note.tags.toSet()

                checkboxContainer.children.forEachIndexed { index, view ->
                        if ((view as CheckBox).isChecked) t1.add(tags[index])
                }

                val tagsToAdd = t1.minus(t2).toList()
                val tagsToRemove = t2.minus(t1).toList()

                if (note.note.noteId != null && (tagsToAdd.isNotEmpty() || tagsToRemove.isNotEmpty())) {
                    if (tagsToAdd.isNotEmpty()) noteViewModel.insertTagsForNote(
                        note.note,tagsToAdd
                    )
                    if (tagsToRemove.isNotEmpty()) noteViewModel.deleteTagsForNote(
                        note.note,tagsToRemove
                    )

                }

                val newNoteWithTags = NoteWithTags(note.note, t1.toList())
                noteViewModel.selectedNote.postValue(newNoteWithTags)
            }

            noteViewModel.allTags.observe(viewLifecycleOwner,  Observer { newTags ->
                checkboxContainer.removeAllViews()
                tags = newTags.toMutableList()
                tags.forEach {
                    checkboxContainer.addView(CheckBox(context).apply {
                        text = it.name
                        isChecked = note.tags.contains(it)
                    })
                }
            })

            this
        }
    }

    override fun onDestroy() {
        Log.e("DialogTag", "On destroy")
        super.onDestroy()
    }
}
