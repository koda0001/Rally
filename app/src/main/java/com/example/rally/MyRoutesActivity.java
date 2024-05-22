package com.example.rally;

import android.os.Bundle;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyRoutesActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);

        RecyclerView routesList = findViewById(R.id.routes);
        RoutesAdapter routesAdapter = new RoutesAdapter();
        Button menu = findViewById(R.id.menu_button);

        routesList.setLayoutManager(new LinearLayoutManager(MyRoutesActivity.this));
        routesList.setAdapter(routesAdapter);

        databaseUtils.getRoutes(routesAdapter::setRoutesData);

        menu.setOnClickListener(view -> {
            finish();
        });

    }
}


