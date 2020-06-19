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

    /**
     * Called when the user taps the Send button
     */
    public void login(View view) {
        EditText editText = (EditText) findViewById(R.id.password);
        String password = editText.getText().toString();

        try {
            App app = (App) getApplicationContext();
            if (app.verifyPassword(password)) {
                Intent intent = new Intent(this, NoteActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        } catch (AppException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
