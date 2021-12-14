package com.example.notesapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class NotesRepo {
    private final NotesDao notesDao;
    private final NotesDatabase database;
    private final Executor executor;

    public NotesRepo(Application application) {
        database = NotesDatabase.getInstance(application);
        notesDao = database.notesDao();
        executor = database.getExecutor();


    }

    public LiveData<List<Note>> allNotes() {
        return notesDao.getAllNotes();
    }

    public LiveData<List<Note>> allQueryNotes(String newText) {
        return notesDao.querySearch(newText);
    }

    public void insertNoteTask(Note note) {
        executor.execute(() -> {
            notesDao.insertNote(note);
            Log.i("Executor", Thread.currentThread().getName());
        });

    }

    public void deleteNoteTask(Note note) {
        executor.execute(() -> {
            notesDao.deleteNote(note);
            Log.i("Executor", Thread.currentThread().getName());
        });

    }

    public void upNoteTask(Note note) {
        executor.execute(() -> {
            notesDao.update(note);
            Log.i("Executor", String.valueOf(Thread.currentThread().getName()));
        });

    }
}
