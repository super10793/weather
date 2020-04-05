package com.example.android.architecture.blueprints.todoapp.data;

import androidx.room.Entity;

import java.io.Serializable;

public final class Weather implements Serializable {
    private String success;
    private Records records;
    public String getSuccess() {
        return success;
    }
    public Records getRecords() {
        return records;
    }

    public class Records implements Serializable {
        private String datasetDescription;
        private WeatherLocation[] location;
        public String getDatasetDescription() {
            return datasetDescription;
        }
        public WeatherLocation[] getLocation() {
            return location;
        }
    }
}
