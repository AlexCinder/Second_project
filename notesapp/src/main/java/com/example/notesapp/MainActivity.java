package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView rvNotes;
    private NotesAdapter adapter;
    private final List<Note> notes = new ArrayList<>();
    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        rvNotes = findViewById(R.id.note);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotesAdapter(notes);
        getItemTouchHelper().attachToRecyclerView(rvNotes);
        rvNotes.setAdapter(adapter);
        getData();

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                Intent intent = new Intent(MainActivity.this, AddNote.class);
                intent.putExtra("intent", notes.get(position));
                intent.putExtra("state", false);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.id_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                viewModel.setFilter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void addNote(View view) {
        Intent intent = new Intent(this, AddNote.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TAG", String.valueOf(notes.size()));
    }


    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());

            }
        });
    }

    private void remove(int position) {
        Note note = notes.get(position);
        viewModel.deleteNoteTask(note);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {
        LiveData<List<Note>> notesFromDB = viewModel.getLiveNotes();
        notesFromDB.observe(this, fromDB -> {
            Log.i("TAG", "getData");
//            adapter.showChanges(notes, fromDB);
            notes.clear();
            notes.addAll(fromDB);
            adapter.notifyDataSetChanged();
        });
    }


}