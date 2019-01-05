package com.shivam.lists

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast

class NotesInputActivity : AppCompatActivity() {
    lateinit var mIntent : Intent
    lateinit var noteEditText : EditText
    lateinit var floatingActionButton: FloatingActionButton

    private fun onClickOk(){
        if (noteEditText.text.isEmpty()){
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_LONG).show()
        }
        else {
            mIntent = Intent()
            mIntent.putExtra("note", noteEditText.text.toString())
            setResult(Activity.RESULT_OK, mIntent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        noteEditText = findViewById(R.id.noteEditText)
        noteEditText.requestFocus()
        // Finish Activity when Enter key is pressed on the keyboard
        noteEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                onClickOk()
                return@OnKeyListener true
            }
            false
        })
        floatingActionButton = findViewById(R.id.floatingButton)
        if (intent.hasExtra("note")){
            noteEditText.setText(intent.getStringExtra("note"))
        }
        floatingActionButton.setOnClickListener{
           onClickOk()
        }
    }
}
