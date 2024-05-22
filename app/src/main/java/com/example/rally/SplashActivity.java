package com.example.rally;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.data.LoginData;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LoginData loginData = App.cacheUtils.getLoginData();

        if (loginData != null && !loginData.login.isEmpty() && !loginData.password.isEmpty()) {
            databaseUtils.loginUser(
                    loginData.login,
                    loginData.password,
                    user -> startNewActivity(MainActivity.class)
            );
        }

        findViewById(R.id.loginButton).setOnClickListener(view -> {
            startNewActivity(LoginActivity.class);
        });
    }

    private void startNewActivity(Class newActivity) {
        startActivity(new Intent(this, newActivity));
    }
}