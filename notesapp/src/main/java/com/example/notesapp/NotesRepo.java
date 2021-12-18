package com.example.notesapp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    private final FirebaseAuth user;
    private static final String TAG = "TAG";
    private final Map<String, Note> fireNotes;

    public NotesRepo(Application application) {
        database = NotesDatabase.getInstance(application);
        notesDao = database.notesDao();
        executor = database.getExecutor();
        fireStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance();
        fireNotes = new HashMap<>();


    }

//    public void upNoteRemote(long id, Note note) {
//        fireStore.collection("notes")
//               .add(note);
//    }

//    public FirebaseFirestore getFireStore() {
//        return fireStore;
//    }

    public LiveData<List<Note>> allNotes() {
        return notesDao.getAllNotes();
    }

    public LiveData<List<Note>> allQueryNotes(String newText) {
        return notesDao.querySearch(newText);
    }

    public void insertNoteTask(Note note) {

        executor.execute(() -> notesDao.insertNote(note));
    }

    public void deleteNoteTask(Note note) {
        executor.execute(() -> notesDao.deleteNote(note));

    }

    public void upNoteTask(Note note) {
        executor.execute(() -> notesDao.update(note));

    }

    public void syncDataRemote(List<Note> note) {
        fireNotes.clear();
        for (Note n : note) {
            fireNotes.put(String.valueOf(n.getId()), n);
        }
        fireStore.collection("notes")
                .document(user.getCurrentUser().getUid()).set(fireNotes)
                .addOnFailureListener(e -> Log.d(TAG, "onFailure:  " + user
                        .getCurrentUser().getUid()));

    }


}
