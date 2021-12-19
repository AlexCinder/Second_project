package com.example.notesapp;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class NotesRepo {
    private final NotesDao notesDao;
    private final NotesDatabase database;
    private final Executor executor;
    private final FirebaseFirestore fireStore;
    private final FirebaseAuth mAuth;
    private static final String TAG = "TAG";
    private final Map<String, List<Note>> fireNotes;
    private List<Note> notes;

    public NotesRepo(Application application) {
        database = NotesDatabase.getInstance(application);
        notesDao = database.notesDao();
        executor = database.getExecutor();
        fireStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fireNotes = new HashMap<>();
        notes = new ArrayList<>();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Note> getCloudData() {
        if (mAuth.getCurrentUser() != null) {
            fireStore.collection("notes")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            notes = documentSnapshot
                                    .toObject(NoteList.class)
                                    .getNoteLists();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());

                }
            });
        }
        return notes;
    }

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
        if (mAuth.getCurrentUser() != null) {
        fireNotes.put("notes of " + mAuth
                .getCurrentUser()
                .getEmail(), note);}
        fireStore.collection("notes")
                .document(mAuth.getCurrentUser().getUid()).set(fireNotes)
                .addOnFailureListener(e -> Log.d(TAG, "onFailure:  " + mAuth
                        .getCurrentUser().getUid()));

    }

    public void deleteNotes() {
        executor.execute(notesDao::deleteAllNotes);
    }

    public void signOut() {
        deleteNotes();
        mAuth.signOut();
    }
    public boolean checkUser(){
        return  mAuth.getCurrentUser() != null;
    }

}
