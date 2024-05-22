package com.example.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum AchievementType implements Serializable {
    @SerializedName("routes_traveled")
    routes_traveled("routes_traveled"),
    @SerializedName("kilometers_traveled")
    kilometers_traveled("kilometers_traveled"),
    @SerializedName("v_max")
    v_max("v_max"),
    @SerializedName("time_with_more_than_120_km_per_hour")
    time_with_more_than_120_km_per_hour("time_with_more_than_120_km_per_hour");

    public final String label;

    AchievementType(String label) {
        this.label = label;
    }

    @NonNull
    @Override
    public String toString() {
        return label;
    }
}
