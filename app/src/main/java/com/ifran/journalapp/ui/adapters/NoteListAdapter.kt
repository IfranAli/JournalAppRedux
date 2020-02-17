package com.ifran.journalapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.ifran.journalapp.R
import com.ifran.journalapp.persistence.NoteWithTags
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import java.util.Date
import java.text.DateFormat

private val mMonths = arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

class NoteListAdapter internal constructor( context: Context )
    : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var notesWithTag = emptyList<NoteWithTags>()
    private val resources = context.resources

    private val colorNorm = resources.getColor(R.color.colorAccent, null)
    private val colorSelected = resources.getColor(R.color.colorPrimary, null)

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    inner class NoteViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var day: TextView = itemView.item_day
        var month: TextView = itemView.item_month
        var time: TextView = itemView.item_time
        var title: TextView = itemView.item_title
        var preview: TextView = itemView.item_preview

        fun bind(noteWithTags: NoteWithTags, isActivated: Boolean = false) {
            val note = noteWithTags.note
            preview.text = note.content
            title.text = note.title

            val date: Date? = note.createdTime?.let { Date(it * 1000L) }
            date?.let {
                day.text = date.date.toString()
                month.text = mMonths[date.month]
                time.text = DateFormat.getInstance().format(date)

                itemView.graphics_datebox.setBackgroundColor(when (isActivated) {
                    true -> colorSelected
                    false -> colorNorm
                })
            }
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesWithTag[position]

        tracker?.let {
            holder.bind(note, it.isSelected(note.note.noteId))
        }
    }

    internal fun setNotes( notesWithTag: List<NoteWithTags>) {
        this.notesWithTag = notesWithTag
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = notesWithTag.size

    fun getItem(position: Int): NoteWithTags = notesWithTag[position]

    fun getPosition(note: NoteWithTags): Int = notesWithTag.indexOf(note)

    override fun getItemId(position: Int): Long = notesWithTag[position].note.noteId!!
}