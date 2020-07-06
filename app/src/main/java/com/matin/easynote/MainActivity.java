package com.matin.easynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.matin.easynote.recAdapter.NoteAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {


    public static RecyclerView recyclerView;
    public static ArrayList<String> notes = new ArrayList<>();
    public static ArrayList<String> notesTitle = new ArrayList<>();
    static NoteAdapter adapter;
    public static ImageView background;
    boolean layoutText = false;     //false == gridView
    SharedPreferences sharedPreferences;


    //add menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.note_layout){

                //change layout to opposite layout and change icon
                if(layoutText){ //true == listView
                    //if listView
                    layoutText = false;
                    item.setIcon(R.drawable.ic_grid);
                    featuredRecycler(LinearLayout.VERTICAL, R.layout.note_card_list, false);
                }else{
                    //grid view
                    layoutText = true;
                    item.setIcon(R.drawable.ic_list);
                    featuredRecycler(GridLayout.VERTICAL, R.layout.note_card_grid, true);
                }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        background = findViewById(R.id.imageView);


        //shared preference for saving application data
        sharedPreferences = getApplicationContext().getSharedPreferences("com.matin.easynote", MODE_PRIVATE);

        //get sharedPreference data and put to hashSet
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        if(set == null){
            //if nothing saved i.e app opens up for the first time
            notes.add("Example Note");
            recyclerView.setBackgroundResource(0);
            background.setVisibility(View.VISIBLE);

        }else if(set.isEmpty()){    //if all notes has been deleted
            recyclerView.setBackgroundResource(R.drawable.empty_list);
            animateBackground();   //animation

            background.setVisibility(View.INVISIBLE);

        }else{
            notes = new ArrayList<>(set);  //pass stored values in hashSet to arrayList
            animateBackground();

            recyclerView.setBackgroundResource(0);
            background.setVisibility(View.VISIBLE);
        }

        //limit amount of text to be shown in recyclerView
        for(String current : notes){
            if(current.length() > 10){
                notesTitle.add(current.substring(0, 10) + "...");
            }else{
                notesTitle.add(current);
            }
        }

        //fab button to write new note in NoteEditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create new note
                //open NoteEditorActivity
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                startActivity(intent);

            }
        });

        //default layout (vertical)
        featuredRecycler(LinearLayout.VERTICAL, R.layout.note_card_list, false);

    }

    /**
     *
     * @param layout is the display layout (LinearLayout || GridLayout)
     * @param cardView  the card for respective layouts (note_card_grid for GridLayout) || (note_card_list for LinearLayout)
     * @param layoutView    to tell which view to set to recyclerView (true == GridLayout) || (false == LinearLayout)
     */
    public void featuredRecycler(int layout, int cardView, boolean layoutView){
        recyclerView.setHasFixedSize(true);

        if(layoutView){ //false == LinearLayout
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, layout, false));
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), layout, false));
        }

        //adapter
        adapter = new NoteAdapter(this, notesTitle, cardView);
        recyclerView.setAdapter(adapter);

        animateBackground();
    }

    //animation
    public static void animateBackground(){
        recyclerView.setAlpha(0);
        recyclerView.animate().alpha(1).setDuration(1500);
    }

}
