package com.example.makenotes.activities.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.makenotes.R;
import com.example.makenotes.activities.adapter.MyListAdapter;
import com.example.makenotes.activities.database.Database;
import com.example.makenotes.activities.entity.note;
import com.example.makenotes.activities.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;
    public RecyclerView noteRecyclerView;
    public ArrayList<note> noteList;
    public MyListAdapter adapter;
    ImageView addNote;
    Database database;
    private int NoteClickPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(getApplicationContext(), PrepareNote.class), REQUEST_CODE_ADD_NOTE);
                        // startNewActivity();
                    }
                });

        //MyListAdapter newList=new MyListAdapter();
        noteRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        noteList = new ArrayList<note>();

        adapter = new MyListAdapter(noteList, this);
        // noteRecyclerView.setHasFixedSize(false);
        noteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteRecyclerView.setAdapter(adapter);
        getNotes(REQUEST_CODE_SHOW_NOTES, false);
    }

        void startNewActivity() {

        Intent intent = new Intent(this, PrepareNote.class);
        startActivity(intent);
    }

    public void getNotes(final int requestcode, final boolean isNoteDeletd) {
        @SuppressLint("StaticFieldLeak")
        class getNotesText extends AsyncTask<Void, Void, List<note>> {
            @Override
            protected List<note> doInBackground(Void... voids) {
                return (Database.getDatabase(getApplicationContext()).noteDao().getAllNotes());
            }

            @Override
            protected void onPostExecute(List<note> noteLists) {
                super.onPostExecute(noteLists);


                if (requestcode == REQUEST_CODE_SHOW_NOTES) {
                    noteList.addAll(noteLists);
                    adapter.notifyDataSetChanged();
                } else if (requestcode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, noteLists.get(0));
                    adapter.notifyItemInserted(0);
                    Log.i("already presen ", noteList.toString());
                    noteRecyclerView.smoothScrollToPosition(0);
                } else if (requestcode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(NoteClickPosition);
                if (isNoteDeletd) {
                    adapter.notifyItemChanged(NoteClickPosition);
                } else {
                    noteLists.add(NoteClickPosition, noteLists.get(NoteClickPosition));
                    adapter.notifyItemChanged(NoteClickPosition);
                }
            }
        }

    }
        new getNotesText().execute();
}

    @Override
    public void onNoteClicked(note Note, int position) {
        NoteClickPosition = position;
        Intent intent = new Intent(getApplicationContext(), PrepareNote.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("Note", Note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }
}