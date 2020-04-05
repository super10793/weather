package com.example.android.architecture.blueprints.todoapp.data;

import androidx.room.Entity;

import java.io.Serializable;

public final class WeatherLocation implements Serializable {
    private String locationName;
    private WeatherElement[] weatherElement;
    public String getLocationName() {
        return locationName;
    }
    public WeatherElement[] getWeatherElement() {
        return weatherElement;
    }

    public class WeatherElement implements Serializable {
        private String elementName;
        private Time[] time;
        public String getElementName() {
            return elementName;
        }
        public Time[] getTime() {
            return time;
        }
    }

    public class Time implements Serializable {
        private String startTime;
        private String endTime;
        private Parameter parameter;
        public String getStartTime() {
            return startTime;
        }
        public String getEndTime() {
            return endTime;
        }
        public Parameter getParameter() {
            return parameter;
        }
    }

    public class Parameter implements Serializable {
        private String parameterName;
        private String parameterUnit;
        public String getParameterName() {
            return parameterName;
        }
        public String getParameterUnit() {
            return parameterUnit;
        }
    }
}
