package com.example.notesapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddNoteViewModel extends AndroidViewModel {

    private final NotesRepo notesRepo;
//    private final FirebaseFirestore fireBase;


    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        notesRepo = new NotesRepo(application);
//        fireBase = notesRepo.getFireStore();

    }

    public void upNoteRemote(long id, Note note) {
        notesRepo.upNoteRemote(id, note);
    }

    public void upNoteTask(Note note) {
        notesRepo.upNoteTask(note);

    }


    public void insertNoteTask(Note note) {
         notesRepo.insertNoteTask(note);

    }


}
