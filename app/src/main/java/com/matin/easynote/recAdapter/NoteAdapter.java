package com.matin.easynote.recAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.matin.easynote.MainActivity;
import com.matin.easynote.NoteEditorActivity;
import com.matin.easynote.R;

import java.util.ArrayList;
import java.util.HashSet;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

    ArrayList<String> notes;
    boolean starredPosition = false;

    Context ct;
    int cardLayout;

    public NoteAdapter(Context ct, ArrayList<String> notes, int cardLayout){
        this.ct = ct;
        this.notes = notes;
        this.cardLayout = cardLayout;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(cardLayout, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteViewHolder holder, int position) {
        final int current = position;

        //display notes
        holder.notesTextView.setText(notes.get(position));

        // add to stared note when star is clicked
        holder.starredText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(starredPosition){
                    holder.starredText.setBackgroundResource(R.drawable.ic_star);
                    starredPosition = false;
                }else
                {
                    holder.starredText.setBackgroundResource(R.drawable.ic_star_yellow);
                    starredPosition = true;
                }

            }
        });

        //when note item is clicked open note in NoteEditorActivity activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open NoteEditorActivity and and pass index position
                Intent intent = new Intent(ct, NoteEditorActivity.class);
                intent.putExtra("noteId", current);
                intent.putExtra("starred", starredPosition);

                ct.startActivity(intent);

            }
        });

        //delete item on longClick
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View v2 = v;
                new AlertDialog.Builder(ct)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure you want to delete this note?")
                        .setMessage("This action cannot be undone!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove(current);
                                MainActivity.notes.remove(current);

                                notifyDataSetChanged();

                                //save data in sharedPreference
                                SharedPreferences sharedPreferences = ct.getSharedPreferences("com.matin.easynote", Context.MODE_PRIVATE);
                                HashSet<String> set = new HashSet<>(notes);
                                sharedPreferences.edit().putStringSet("notes", set).apply();

                                //change display image
                                if(set.isEmpty()){
                                    MainActivity.recyclerView.setBackgroundResource(R.drawable.empty_list);
                                    MainActivity.animateBackground();
                                    MainActivity.background.setVisibility(View.INVISIBLE);

                                }else{
                                    MainActivity.recyclerView.setBackgroundResource(0);
                                    MainActivity.background.setVisibility(View.VISIBLE);
                                }

                                Snackbar.make(v2, "Deleted", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView notesTextView;
        TextView starredText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            notesTextView = itemView.findViewById(R.id.notesTextView);
            starredText = itemView.findViewById(R.id.starredText);

        }
    }
}
