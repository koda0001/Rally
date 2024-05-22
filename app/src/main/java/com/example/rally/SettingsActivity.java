package com.example.rally;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        findViewById(R.id.backButton3).setOnClickListener(view -> {
            finish();
        });

        findViewById(R.id.degreeButton).setOnClickListener(view -> {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            if (buttonText.equals("Pokaż temperature w °C")) {
                button.setText("Pokaż temperature w °F");
            } else if (buttonText.equals("Pokaż temperature w °F")) {
                button.setText("Pokaż temperature w °C");
            }
        });

        findViewById(R.id.metricalButton).setOnClickListener(view -> {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            if (buttonText.equals("Pokaż wartość w metrach")) {
                button.setText("Pokaż wartość w milach");
            } else if (buttonText.equals("Pokaż wartość w milach")) {
                button.setText("Pokaż wartość w metrach");
            }
        });

        findViewById(R.id.removeAccountButton).setOnClickListener(view -> {
            databaseUtils.removeUser((object) -> {
                App.cacheUtils.commitUser(null);
                App.cacheUtils.commitLoginData(null);
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        });
    }
}