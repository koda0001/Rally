package com.example.rally;

import android.content.Intent;
import android.os.Bundle;

public class RegisterConfirmationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_confirmation);

        findViewById(R.id.zaloguj_button).setOnClickListener(view -> {
            Intent intent = new Intent(RegisterConfirmationActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}