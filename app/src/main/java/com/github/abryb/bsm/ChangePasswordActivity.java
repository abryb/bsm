package com.github.abryb.bsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    public void changePassword(View view) {
        EditText editText = findViewById(R.id.password);
        String password = editText.getText().toString();


        try {
            App app = (App) getApplicationContext();
            app.changePassword(password);
            Toast.makeText(this, "Password changed. Thank you.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, NoteActivity.class);
            startActivity(intent);

        } catch (InsufficientPasswordException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            editText.setText("");
        } catch (AppException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
