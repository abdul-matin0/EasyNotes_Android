package com.matin.easynote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {

    //share note and categorize note

    EditText editText;
    TextView deleteNoteTextView, shareNoteTextView;
    int noteId;
    SharedPreferences sharedPreferences;
    HashSet<String> set;
    TextView starredText;
    boolean starredPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.matin.easynote", MODE_PRIVATE);

        editText = findViewById(R.id.editText);
        deleteNoteTextView = findViewById(R.id.deleteNoteTextView);
        starredText = findViewById(R.id.addStarredTextView);
        shareNoteTextView = findViewById(R.id.shareNoteTextView);


        //get intent data from NoteAdapter (recyclerView item onclick)
        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);
        starredPosition = intent.getBooleanExtra("starred", false);

        //set starredNote
        if(starredPosition){
            starredPosition = false;
            starredText.setBackgroundResource(R.drawable.ic_star_yellow);
        }else
        {
            starredPosition = true;
            starredText.setBackgroundResource(R.drawable.ic_star);

        }


        if(noteId != -1){   //if user is not creating a new note
            //display selected note text in editText
            editText.setText(MainActivity.notes.get(noteId));
        }else{
            //creating new notes
            MainActivity.notes.add("");
            MainActivity.notesTitle.add("");

            noteId = MainActivity.notes.size() - 1;     //get created note index
        }

        //on editing editText widget
        //when you update the note/ add to the note, get the updated text and set to index in notes arrayList
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = String.valueOf(s);

                if(current.trim().isEmpty()){
                    // if you clean all get text
                    MainActivity.notes.set(noteId, "Empty note...");
                    MainActivity.notesTitle.set(noteId, "Empty Note...");

                    //update notesTitle ArrayList
                    MainActivity.adapter.notifyDataSetChanged();

                }else{
                    //add / update edited text to notes arrayList
                    MainActivity.notes.set(noteId, current);
                    MainActivity.notesTitle.set(noteId, current);

                    limitText(noteId, current);

                    MainActivity.adapter.notifyDataSetChanged();
                }

                //save data in sharedPreference hashSet
                saveAndUpdate(MainActivity.notes);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //onclick delete icon
        deleteNoteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show alert dialog to confirm delete note
                new AlertDialog.Builder(NoteEditorActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure you want to delete this note?")
                        .setMessage("This action cannot be undone!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.notes.remove(noteId);
                                MainActivity.notesTitle.remove(noteId);

                                MainActivity.adapter.notifyDataSetChanged();

                                //save data in sharedPreference set
                               saveAndUpdate(MainActivity.notes);
                               MainActivity.animateBackground();

                                //go back to MainActivity
                                finish();

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


        //star note and save
        starredText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(starredPosition){
                    starredPosition = false;
                    starredText.setBackgroundResource(R.drawable.ic_star);
                }else
                {
                    starredPosition = true;
                    starredText.setBackgroundResource(R.drawable.ic_star_yellow);
                }
            }
        });

        //shareNote
        shareNoteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNote();
            }
        });

    }

    //save and update in sharedPreference
    public void saveAndUpdate(ArrayList<String> editNote){

        //save data in sharedPreference set
        set = new HashSet<>(editNote);
        sharedPreferences.edit().putStringSet("notes", set).apply();

        //change display image
        if(set.isEmpty()){
            MainActivity.recyclerView.setBackgroundResource(R.drawable.empty_list);
            MainActivity.background.setVisibility(View.INVISIBLE);

        }else{
            MainActivity.recyclerView.setBackgroundResource(0);
            MainActivity.background.setVisibility(View.VISIBLE);
        }
    }


    public void limitText(int noteLimitId, String current){

        //update notesTitle ArrayList
        if(current.length() > 10){
            MainActivity.notesTitle.set(noteLimitId, current.substring(0, 10) + "...");
        }else{
            MainActivity.notesTitle.set(noteLimitId, current);
        }

    }

    //share note
    public void shareNote(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = String.valueOf(editText.getText());


        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Easy Note");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    //go back to MainActivity on clicking support actionBar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
