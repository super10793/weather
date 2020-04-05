/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.data.source.remote;

import android.os.Handler;
import androidx.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.data.Location;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.Weather;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private TasksRemoteDataSource() {}

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public void getWeather(@NonNull String id, final  @NonNull GetWeatherCallback callback) {
        OkHttpClient client = new OkHttpClient();
        final String URL = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001";
        HttpUrl.Builder hb = HttpUrl.parse(URL).newBuilder();
        hb.addQueryParameter("Authorization", "CWB-2D7A3932-6CC1-4BB9-9C7D-F0D27B3A91FB");
        hb.addQueryParameter("format", "JSON");
        hb.addQueryParameter("locationName", "臺北市");
        hb.addQueryParameter("elementName", "MinT");
        final Request request = new Request.Builder().url(hb.build()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Weather weather = new Gson().fromJson(response.body().string(), Weather.class);
                if(weather.getSuccess().equals("true")){
                    Location location = new Location("taipei", new Gson().toJson(weather.getRecords().getLocation()[0].getWeatherElement()));
                    callback.onSuccess(location);
                }else{
                    callback.onError();
                }
            }
        });
    }

    @Override
    public void saveWeather(@NonNull Location location) {

    }
}
