package com.github.abryb.bsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        EditText editText = findViewById(R.id.password);
        String password = editText.getText().toString();

        try {
            App app = (App) getApplicationContext();
            if (app.verifyPassword(password)) {
                Intent intent = new Intent(this, NoteActivity.class);
                startActivity(intent);
            } else {
                editText.setText("");
                Toast.makeText(this, "Wrong password.", Toast.LENGTH_SHORT).show();
            }
        } catch (AppException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
