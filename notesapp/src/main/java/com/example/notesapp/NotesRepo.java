package com.example.notesapp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class NotesRepo {
    private final NotesDao notesDao;
    private final NotesDatabase database;
    private final Executor executor;
    private final FirebaseFirestore fireStore;
    private final Map<Integer, Note> fireNotes;

    public NotesRepo(Application application) {
        database = NotesDatabase.getInstance(application);
        notesDao = database.notesDao();
        executor = database.getExecutor();
        fireStore = FirebaseFirestore.getInstance();
        fireNotes = new HashMap<>();


    }

    public void upNoteRemote(int id, Note note) {
        fireNotes.put(id, note);
        syncData();
    }

    public FirebaseFirestore getFireStore() {
        return fireStore;
    }

    public LiveData<List<Note>> allNotes() {
        return notesDao.getAllNotes();
    }

    public LiveData<List<Note>> allQueryNotes(String newText) {
        return notesDao.querySearch(newText);
    }

    public long insertNoteTask(Note note) {
        long[] id = new long[1];
        executor.execute(() -> id[0] = notesDao.insertNote(note));
        return id[0];
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

    private void syncData() {
        fireStore.collection("notes").add(fireNotes)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(@NonNull DocumentReference documentReference) {
               Log.i("TAG","Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG","Fail");
            }
        });
    }
}
