package com.example.rally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginFailedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_failed);

        findViewById(R.id.try_again_button).setOnClickListener(createOnClickListener(LoginActivity.class));

        findViewById(R.id.register_button).setOnClickListener(createOnClickListener(RegisterActivity.class));

        findViewById(R.id.forgotten_password).setOnClickListener(createOnClickListener(Przywracanie_Hasla.class));
    }

    private View.OnClickListener createOnClickListener(Class<?> newActivity) {
        return v -> startActivity(new Intent(LoginFailedActivity.this, newActivity));
    }
}