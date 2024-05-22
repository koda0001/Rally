package com.example.rally;

import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.Achievement;
import com.example.data.AchievementType;
import com.example.data.User;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends BaseActivity {

    private User user;
    private final AchievementsAdapter achievementsAdapter = new AchievementsAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        findViewById(R.id.back).setOnClickListener(v -> { finish(); });

        user = App.cacheUtils.getUser();

        RecyclerView achievements = findViewById(R.id.achievements);
        achievements.setAdapter(achievementsAdapter);
        achievements.setLayoutManager(new LinearLayoutManager(AchievementsActivity.this));
        databaseUtils.getAchievements(achievementsFromApi -> {
            List<Pair<Achievement, Number>> achievementsWithProgress = new ArrayList<>();
            for (Achievement achievement : achievementsFromApi) {
                Number progress = 0;
                if (AchievementType.v_max.equals(achievement.achievementType)) {
                    progress = user.maxSpeed;
                } else if (AchievementType.kilometers_traveled.equals(achievement.achievementType)) {
                    progress = user.kilometersTraveled;
                } else if (AchievementType.routes_traveled.equals(achievement.achievementType)) {
                    progress = user.routesTraveled;
                } else if (AchievementType.time_with_more_than_120_km_per_hour.equals(achievement.achievementType)) {
                    progress = user.timeWithMoreThan120KmPerHour;
                }
                achievementsWithProgress.add(new Pair<>(achievement, progress));
            }
            achievementsAdapter.setAchievementsData(achievementsWithProgress, AchievementsActivity.this);
        });
    }
}
