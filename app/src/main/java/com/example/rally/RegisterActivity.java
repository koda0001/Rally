package com.example.rally;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class RegisterActivity extends BaseActivity {

    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejestracja);

        email = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword2);


        findViewById(R.id.rejestracja_button2).setOnClickListener(v -> {
            // Set the user's username and password

            databaseUtils.registerUser(
                    email.getText().toString(),
                    password.getText().toString(),
                    o -> showAlert()
            );
        });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(getResources().getString(R.string.successful_sign_up))
                .setMessage(getResources().getString(R.string.you_can_login))
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}