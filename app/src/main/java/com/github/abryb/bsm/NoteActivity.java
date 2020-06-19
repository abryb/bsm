package com.github.abryb.bsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App) getApplicationContext();
        setContentView(R.layout.activity_note);
        try {
            ((EditText) findViewById(R.id.note)).setText(app.getNote());
        } catch (AppException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveNote(View view) {
        EditText editText = findViewById(R.id.note);
        String note = editText.getText().toString();

        try {
            App app = (App) getApplicationContext();
            app.saveNote(note);
            Toast.makeText(this, "Note saved.", Toast.LENGTH_SHORT).show();

        } catch (AppException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void changePassword(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }
}
