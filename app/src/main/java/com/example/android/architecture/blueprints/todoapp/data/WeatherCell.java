package com.example.android.architecture.blueprints.todoapp.data;


import java.io.Serializable;

public final class WeatherCell implements Serializable {
    private int type;
    private WeatherLocation.Time weatherTime;

    public WeatherCell(int type, WeatherLocation.Time weatherTime) {
        this.type = type;
        this.weatherTime = weatherTime;
    }

    public int getType() {
        return type;
    }

    public WeatherLocation.Time getWeatherTime() {
        return weatherTime;
    }

    public enum CellType {
        TEXT,
        IMAGE
    }
}
