package com.example.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("name")
    public String name;
    @SerializedName("routes_traveled")
    public double routesTraveled;
    @SerializedName("max_speed")
    public double maxSpeed;
    @SerializedName("kilometers_traveled")
    public double kilometersTraveled;
    @SerializedName("time_with_more_than_120_km_per_hour")
    public double timeWithMoreThan120KmPerHour;

    public User(String name, Integer routesTraveled, Integer maxSpeed, Integer kilometersTraveled, Integer timeWithMoreThan120KmPerHour) {
        this.name = name;
        this.routesTraveled = routesTraveled;
        this.maxSpeed = maxSpeed;
        this.kilometersTraveled = kilometersTraveled;
        this.timeWithMoreThan120KmPerHour = timeWithMoreThan120KmPerHour;
    }
}
