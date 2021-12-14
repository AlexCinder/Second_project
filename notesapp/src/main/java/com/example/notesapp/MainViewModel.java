package com.example.notesapp;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.Executor;

public class MainViewModel extends AndroidViewModel {


    private final LiveData<List<Note>> allNotes;
    private  MutableLiveData<String> allQueryNotes;
    private final NotesRepo notesRepo;


    public MainViewModel(@NonNull Application application) {
        super(application);
        notesRepo = new NotesRepo(application);
        allQueryNotes = new MutableLiveData<>("");
        allNotes = Transformations.switchMap(allQueryNotes, new Function<String, LiveData<List<Note>>>() {
            @Override
            public LiveData<List<Note>> apply(String input) {
                Log.i("TAG", "getData");
                if (TextUtils.isEmpty(input)){
                return notesRepo.allNotes();}
                else return notesRepo.allQueryNotes(input);
            }
        });


    }

    public LiveData<List<Note>> getLiveNotes() {
        return allNotes;
    }

    public void setFilter(String query) {
        this.allQueryNotes.setValue(query);
    }


    public void deleteNoteTask(Note note) {
        notesRepo.deleteNoteTask(note);
    }


    @Override
    protected void onCleared() {
        super.onCleared();


    }
}
