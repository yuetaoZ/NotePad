package com.example.notepad;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    List<Note> noteList;  // Main content is here
    private RecyclerView recyclerView; // Layout's recyclerview
    private NotesAdapter notesAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        loadData();
        recyclerView = findViewById(R.id.recycler);
        // Data to recyclerview adapter
        notesAdapter = new NotesAdapter(noteList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("note list", null);
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        noteList = gson.fromJson(json, type);

        if (noteList == null) {
            noteList = new ArrayList<>();
        }
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noteList);
        editor.putString("note list", json);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addItem:
                addNewNote();
                return true;
            case R.id.getInfo:
                getAppInformation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAppInformation() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNewNote() {
        Note newNote = new Note();
        newNote.updateTime();

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("Note", newNote);
        intent.putExtra("Position", -1);

        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    // From OnClickListener
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Note note = noteList.get(pos);
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("Note", note);
        intent.putExtra("Position", pos);

        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Note originalNote;
                    Note updatedNote = (Note) data.getSerializableExtra("Note");

                    int pos = data.getIntExtra("Position", -1);
                    if (pos == -1) {
                        originalNote = new Note();
                    } else {
                        originalNote = noteList.get(pos);
                    }

                    if (updatedNote != null) {
                        updateNote(originalNote, updatedNote, pos);
                    } else {
                        deleteNote(pos);
                    }
                }
            } else {
                Toast.makeText(this, "SECOND ACTIVITY result not OK!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Unexpected code received: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateNote(Note originalNote, Note updatedNote, int pos) {
        if (noteHasChanged(originalNote, updatedNote)) {

            originalNote.setTitle(updatedNote.getNoteTitle());
            originalNote.setNoteText(updatedNote.getNoteText());
            originalNote.updateTime();

            if (pos == -1) {
                noteList.add(0, originalNote);
                notesAdapter.notifyItemInserted(0);
            } else {
                notesAdapter.notifyItemChanged(pos);
            }

            saveData();
            Toast.makeText(this, "Result changed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Result didn't change", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean noteHasChanged(Note originalNote, Note updatedNote) {
        if (originalNote.getNoteTitle().equals(updatedNote.getNoteTitle()) && originalNote.getNoteText().equals(updatedNote.getNoteText())) {
            return false;
        } else {
            return true;
        }
    }

    private void deleteNote(int pos) {
        if (pos != -1) {
            noteList.remove(pos);
            notesAdapter.notifyItemRemoved(pos);
            saveData();
            Toast.makeText(this, "Result changed", Toast.LENGTH_SHORT).show();
        }
    }

    // From OnLongClickListener
    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Note m = noteList.get(pos);
        Toast.makeText(v.getContext(), "LONG " + m.toString(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "The back button was pressed - Bye!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}