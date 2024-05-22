package com.example.rally;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.data.LoginData;

public class LoginActivity extends BaseActivity {

    EditText login_email;
    EditText login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_email = findViewById(R.id.editTextTextEmailAddress);
        login_password = findViewById(R.id.editTextTextPassword);

        findViewById(R.id.zaloguj_button1).setOnClickListener(view -> {
            String username = login_email.getText().toString();
            String password = login_password.getText().toString();
            databaseUtils.loginUser(username, password, parseUser -> {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                App.cacheUtils.commitLoginData(new LoginData(username, password));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            });
        });

        findViewById(R.id.rejestracja_button3).setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}