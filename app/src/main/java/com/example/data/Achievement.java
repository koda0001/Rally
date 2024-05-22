package com.example.data;

public class Achievement {
    public String description;
    public int target;
    public AchievementType achievementType;

    public Achievement(String description, int target, AchievementType achievementType) {
        this.description = description;
        this.target = target;
        this.achievementType = achievementType;
    }
}
