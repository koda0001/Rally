package com.example.rally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.parse.ParseUser;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        findViewById(R.id.nawigacja_button).setOnClickListener(createOnClickListener(PrepareNavigationActivity.class));

        findViewById(R.id.obdButton).setOnClickListener(createOnClickListener(Monitoring_Pojazdu.class));

        findViewById(R.id.myRoutesButton).setOnClickListener(createOnClickListener(MyRoutesActivity.class));

        findViewById(R.id.weatherButton).setOnClickListener(createOnClickListener(WeatherActivity.class));

        findViewById(R.id.achievementsButton).setOnClickListener(createOnClickListener(AchievementsActivity.class));

        findViewById(R.id.settingsButton).setOnClickListener(createOnClickListener(SettingsActivity.class));

        findViewById(R.id.addRouteButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this , NavigationActivity.class);
            intent.putExtra(NavigationActivity.DATA_IS_FOR_ROUTE_ADD, true);
            startActivity(intent);
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            ParseUser.logOut();
            App.cacheUtils.commitLoginData(null);
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        databaseUtils.getUserData(user -> App.cacheUtils.commitUser(user));
    }

    private View.OnClickListener createOnClickListener(Class<?> newActivity) {
        return v -> startActivity(new Intent(MainActivity.this, newActivity));
    }
}