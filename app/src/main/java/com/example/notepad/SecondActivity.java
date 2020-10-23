package com.example.notepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SecondActivity extends AppCompatActivity {

    private Note note = new Note();
    private String oldNoteTitle;
    private String oldNoteText;
    int pos;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        EditText editNoteTitle = findViewById(R.id.editNoteTitle);
        EditText editNoteText = findViewById(R.id.editNoteText);

        Intent intent = getIntent();
        if (intent.hasExtra("Position")) {
            pos = intent.getIntExtra("Position", -1);
        }

        if (intent.hasExtra("Note")) {
            note = (Note) intent.getSerializableExtra("Note");
            if (note != null) {
                oldNoteTitle = note.getNoteTitle();
                oldNoteText = note.getNoteText();
                editNoteTitle.setText(oldNoteTitle);
                editNoteText.setText(oldNoteText);
            }
        } else {
            editNoteTitle.setText("");
            editNoteText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveNote();
        Intent intent = new Intent();
        intent.putExtra("Note", note);
        intent.putExtra("Position", pos);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    private void saveNote() {
        EditText editNoteTitle = findViewById(R.id.editNoteTitle);
        EditText editNoteText = findViewById(R.id.editNoteText);

        String noteTitle = editNoteTitle.getText().toString();
        String noteText = editNoteText.getText().toString();

        if (noteTitle.trim().equals("")) {
            note = null;
        } else {
            note.setTitle(noteTitle);
            note.setNoteText(noteText);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        saveNote();

        Intent intent = new Intent();
        intent.putExtra("Note", note);
        intent.putExtra("Position", pos);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}



