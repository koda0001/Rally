package com.example.rally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Przywracanie_Hasla extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przywracanie_hasla);

        Button myButton = findViewById(R.id.przywracanie_hasla_button1); // Replace with your actual button ID
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Przywracanie_Hasla.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}