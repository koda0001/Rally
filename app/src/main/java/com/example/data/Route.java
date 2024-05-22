package com.example.data;

import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Route {
    public String name;
    public ArrayList<Double> markersLng;
    public ArrayList<Double> markersLog;
    public ArrayList<Double> warningLng;
    public ArrayList<Double> warningsLog;

    public Route(String name, ArrayList<Double> markersLng, ArrayList<Double> markersLog, ArrayList<Double> warningsLng, ArrayList<Double> warningsLog) {
        this.name = name;
        this.markersLng = markersLng;
        this.markersLog = markersLog;
        this.warningLng = warningsLng;
        this.warningsLog = warningsLog;
    }
}
