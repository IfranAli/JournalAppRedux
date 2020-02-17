package com.ifran.journalapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ifran.journalapp.persistence.Note
import com.ifran.journalapp.persistence.NoteWithTags
import com.ifran.journalapp.ui.NoteViewModel
import com.ifran.journalapp.ui.adapters.KeyProvider
import com.ifran.journalapp.ui.adapters.NoteListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notes_list.view.*


class NotesList : Fragment() {
    private val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var tracker: SelectionTracker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    class NoteListDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as NoteListAdapter.NoteViewHolder).getItemDetails()
            }
            return null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        viewPager = activity?.findViewById(R.id.view_pager)!!
        val view = inflater.inflate(R.layout.fragment_notes_list, container, false)

        val adapter = NoteListAdapter(inflater.context)

        val notesList = view.notes_list
        notesList.adapter = adapter
        notesList.layoutManager = LinearLayoutManager(inflater.context)

        tracker = SelectionTracker.Builder<Long>(
            "notes-selection",
            notesList,
            KeyProvider(notesList),
            NoteListDetailsLookup(notesList),
            StorageStrategy.createLongStorage()
            )
            .withOnItemActivatedListener { item, _ ->
                    item.selectionKey?.let {
                        noteViewModel.setSelectedNoteById(item.selectionKey!!)
                        viewPager.currentItem = 1
                    }
                    true
            }
            .build()


        adapter.tracker = tracker

        noteViewModel.allNotesWithTags.observe( viewLifecycleOwner, Observer {
                notes -> notes?.let { adapter.setNotes(it) }
        })

        view.fab.setOnClickListener {
            noteViewModel.selectedNote.value = NoteWithTags(Note("",""), emptyList())
            viewPager.currentItem = 1
        }

        // TODO: Does not work, callbacks are overwritten.
        //  Need to enable/disable callbacks depending on which viewpager page the user is on.
        //  -- OR -- Handle viewpager backpresses in one fragment
        // Clear tracker selection if user pressed back
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.e("Notes", "backpressed")
            if (tracker.hasSelection()) tracker.clearSelection()
        }

        return view
    }

    private fun getIdsFromSelected(): List<Long> {
        val ids = mutableListOf<Long>()
        tracker.selection.forEach { ids.add(it) }
        return ids
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        tracker.addObserver(
            object: SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    menu.findItem(R.id.action_delete).isVisible = tracker.hasSelection()
                    super.onSelectionChanged()
                }
            })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_delete).isVisible = tracker.hasSelection()
        super.onPrepareOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                val noteIds = getIdsFromSelected()
                if (noteIds.isEmpty()) return true
                //noteIds.forEach { Log.e("Deleting", "Note $it") }
                noteViewModel.deleteNotesByIds(getIdsFromSelected())
                tracker.clearSelection()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
