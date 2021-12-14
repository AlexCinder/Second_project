package com.example.notesapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
;

public class AddNote extends AppCompatActivity {
    private EditText edTitle;
    private EditText edDescription;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private AddNoteViewModel viewModel;
    private ImageView im;
    private ConstraintLayout imageCL;
    private FloatingActionButton addImage;
    private ImageButton editImage;
    private ImageButton deleteImage;
    private String uri = "empty";
    private boolean state = true;
    private Note note;


    private final ActivityResultLauncher<Intent> searchImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData().toString();
                        Log.i("TAG", uri);
                        im.setImageURI(result.getData().getData());
                        getContentResolver().takePersistableUriPermission(result.getData().
                                getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
        viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);
        edTitle = findViewById(R.id.editTextTitle);
        edDescription = findViewById(R.id.editTextTextMultiLine);
        spinner = findViewById(R.id.spinner);
        radioGroup = findViewById(R.id.rgPriority);
        imageCL = findViewById(R.id.imageCL);
        addImage = findViewById(R.id.addImage);
        editImage = findViewById(R.id.editButton);
        deleteImage = findViewById(R.id.deleteButton);
        im = findViewById(R.id.im);
        getIntentForEdit();

    }

    private void getIntentForEdit() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("intent") && intent.hasExtra("state")) {
            note = (Note) intent.getSerializableExtra("intent");
            state = intent.getBooleanExtra("state", true);
            if (note != null && !state) {
                edTitle.setText(note.getTitle());
                edDescription.setText(note.getDescription());
                spinner.setSelection(getSpinnerIndex(spinner, note.getDayOfWeek()));
                ((RadioButton) radioGroup.
                        getChildAt(getRadioButtonTag(radioGroup, note.getPriority()))).
                        setChecked(true);
                if (!note.getUri().equals("empty") && !(note.getUri() == null)) {
                    uri = note.getUri();
                    imageCL.setVisibility(View.VISIBLE);
                    im.setImageURI(Uri.parse(note.getUri()));
                }
            }
        }
    }

    public void done(View view) {
        String description = edDescription.getText().toString().trim();
        String title = edTitle.getText().toString().trim();
        int priority = Integer.parseInt(findViewById(radioGroup.getCheckedRadioButtonId()).
                getTag().toString().trim());
        String dayOfWeek = spinner.getSelectedItem().toString().trim();
        if (!title.isEmpty() || !description.isEmpty()) {
            if (state) {
                viewModel.insertNoteTask(new Note(title, description, dayOfWeek, priority, uri));

            } else {
                note.setUri(uri);
                note.setDescription(description);
                note.setTitle(title);
                note.setPriority(priority);
                note.setDayOfWeek(dayOfWeek);
                viewModel.upNoteTask(note);
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            this.finish();
        }
    }

    public void editImage(View view) {
        switch (view.getId()) {
            case (R.id.addImage):
                imageCL.setVisibility(View.VISIBLE);
                addImage.setVisibility(View.GONE);
                break;
            case (R.id.deleteButton):
                im.setImageResource(0);
                uri = "empty";
                imageCL.setVisibility(View.GONE);
                addImage.setVisibility(View.VISIBLE);
                break;
            case (R.id.editButton):
                Intent chooseImage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                chooseImage.setType("image/*");
                searchImage.launch(chooseImage);
                break;


        }


    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;

    }

    private int getRadioButtonTag(RadioGroup radioGroup, int tag) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            if (Integer.parseInt(((RadioButton) (radioGroup.getChildAt(i))).
                    getTag().toString().trim()) == tag)
                return i;
        }
        return 0;
    }
}