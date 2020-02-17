package com.ifran.journalapp

import android.os.Bundle
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import androidx.activity.addCallback
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.ifran.journalapp.persistence.Note
import com.ifran.journalapp.persistence.NoteWithTags
import com.ifran.journalapp.persistence.Tag
import com.ifran.journalapp.ui.NoteViewModel
import kotlinx.android.synthetic.main.fragment_edit_note.view.*
import kotlinx.coroutines.runBlocking


class EditNoteFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var editNoteTitle: EditText
    private lateinit var editNoteContent: EditText
    //private lateinit var editNoteTags: MultiAutoCompleteTextView
    private lateinit var chipGroupTags: ChipGroup
    private var noteWithTags = NoteWithTags(Note("",""), emptyList())
    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {

        return inflater.inflate(R.layout.fragment_edit_note, container, false).run {
            editNoteTitle = note_title
            editNoteContent = note_content
            chipGroupTags = chipGroup_tags
            this
        }
    }

//    private fun createRecipientChip(selectedContact: Contact) {
//        val chip = ChipDrawable.createFromResource(context, R.xml.chip)
//        val span = ImageSpan(chip, 40f, 40f)
//        val cursorPosition: Int = contactAutoCompleteTextView.getSelectionStart()
//        val spanLength: Int = selectedContact.getName().length() + 2
//        val text: Editable = contactAutoCompleteTextView.getText()
//        chip.chipIcon = ContextCompat.getDrawable(
//            this@MainActivity,
//            selectedContact.getAvatarResource()
//        )
//        chip.setText(selectedContact.getName())
//        chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
//        text.setSpan(
//            span,
//            cursorPosition - spanLength,
//            cursorPosition,
//            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
//        )
//    }
//
//    lateinit var adapter: ArrayAdapter<Tag>
//    private fun setupAutoCompleteTagsEditText() {
//        val adapter = ArrayAdapter( context!!, android.R.layout.simple_dropdown_item_1line, tagsArray )
//
//        noteViewModel.allTags.observe( viewLifecycleOwner, Observer { tags -> tags?.let {
//            adapter.run {
//                clear()
//                addAll(tags)
//                notifyDataSetChanged()
//            }
//        }})
//
//        editNoteTags.run {
//            setAdapter(adapter)
//            setTokenizer(CommaTokenizer())
//            threshold = 1
//        }
//
//        editNoteTags.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
//            val selected = adapter.getItem(position)
//            selected?.let {
//                val chip = ChipDrawable.createFromResource(context, R.xml.chip)
//                chip.setText(selected.name)
//                val cursorPosition = editNoteTags.selectionStart
//                val spanLength: Int = selected.name.length + 2
//                val span = ImageSpan(chip)
//                chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
//                editNoteTags.text.setSpan(
//                        span,cursorPosition - spanLength, cursorPosition,
//                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//            }
//        })
//    }

    private fun showDialog() {
        applyChangesToModel()

        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        val prev = fragmentManager!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        // Create and show the dialog.
        val newFragment: DialogFragment = TagsFragment()
        newFragment.show(ft, "dialog")
    }


//    private fun setupChipDrawable() {
//        val chip = ChipDrawable.createFromResource(context, R.xml.chip)
//        chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
//        val span = ImageSpan(chip)
//        val text = editNoteTags.text!!
//        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel.selectedNote.observe(viewLifecycleOwner, Observer { selectedNote ->
            editNoteTitle.setText(selectedNote.note.title)
            editNoteContent.setText(selectedNote.note.content)
            noteWithTags = selectedNote

            chipGroupTags.removeAllViews()

            for (tag in noteWithTags.tags) {
                chipGroupTags.addView(Chip(context).apply {
                    text = tag.name
//                    chipIcon = context.getDrawable(R.drawable.ic_label_black_24dp)
                    isCloseIconVisible = true
                    setChipIconTintResource(R.color.chipIconTint)
                    isClickable = false
                    isCheckable = false
                    //setOnCloseIconClickListener { chipGroupTags.removeView(this as View) }
                } as View)
            }
        })

        viewPager = activity?.findViewById(R.id.view_pager)!!

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                Log.e("EditNote", "backpressed")
                viewPager.currentItem--
                saveNote()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_note_save -> { saveNote(); true }
            R.id.action_tags_manager -> {showDialog(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyChangesToModel(): Boolean {
        val newTitle = editNoteTitle.text.toString()
        val newContent = editNoteContent.text.toString()

        return noteWithTags.note.run {
            when {
                (newContent.isEmpty() && newTitle.isEmpty()) -> false
                (newContent != content || newTitle != title || noteId == null) -> {
                    title = newTitle
                    content = newContent
                    true
                }
                else -> false
            }
        }
    }

    private fun saveNote() {
        if (applyChangesToModel()) {

            noteWithTags.run {
                when {
                    note.noteId != null -> {
                        note.updateLastModified()
                        noteViewModel.insert(note)
                        "Updated existing note"
                    }
                    noteWithTags.tags.isNotEmpty() -> {
                        noteViewModel.insertNoteWithTags(this)
                        "Saved new note with tags"
                    }
                    else -> {
                        noteViewModel.insertSync(note)
                        "Saved new note"
                    }
                }.also { msg ->
                    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
                }
            }
        }
    }
}