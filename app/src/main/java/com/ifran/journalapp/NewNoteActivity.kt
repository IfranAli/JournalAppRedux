package com.ifran.journalapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_note.*


class NewNoteActivity : AppCompatActivity() {

    private lateinit var editNoteTitle: EditText
    private lateinit var editNoteContent: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        editNoteTitle = note_title
        editNoteContent = note_content

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()

            if (TextUtils.isEmpty(editNoteContent.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra(EXTRA_TITLE, editNoteTitle.text.toString())
                replyIntent.putExtra(EXTRA_CONTENT, editNoteContent.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_TITLE = "com.ifran.android.journalapp.TITLE"
        const val EXTRA_CONTENT = "com.ifran.android.journalapp.CONTENT"
        const val RequestCode = 1
    }
}