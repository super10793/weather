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

package com.example.android.architecture.blueprints.todoapp.data.source;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.Location;
import com.example.android.architecture.blueprints.todoapp.data.Weather;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Task> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource,
                                              TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TasksDataSource, TasksDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getWeather(@NonNull String id, @NonNull final GetWeatherCallback callback) {
        mTasksLocalDataSource.getWeather("taipei", new GetWeatherCallback() {
            @Override
            public void onSuccess(Location location) {
                //先顯示舊資料
                callback.onSuccess(location);

                //同時也取得新資料
                getWeatherFromAPI(callback);
            }

            @Override
            public void onError() {
                getWeatherFromAPI(callback);
            }
        });
    }

    @Override
    public void saveWeather(@NonNull Location location) {

    }

    private void getWeatherFromAPI(@NonNull final GetWeatherCallback callback){
        mTasksRemoteDataSource.getWeather("taipei", new GetWeatherCallback() {
            @Override
            public void onSuccess(Location location) {
                mTasksLocalDataSource.saveWeather(location);
                callback.onSuccess(location);
            }

            @Override
            public void onError() {

            }
        });
    }


//    @Override
//    public void fetchWeatherInfo(@NonNull final FetchWeatherCallback callback) {
//        OkHttpClient client = new OkHttpClient();
//        final String URL = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001";
//        HttpUrl.Builder hb = HttpUrl.parse(URL).newBuilder();
//        hb.addQueryParameter("Authorization", "CWB-2D7A3932-6CC1-4BB9-9C7D-F0D27B3A91FB");
//        hb.addQueryParameter("format", "JSON");
//        hb.addQueryParameter("locationName", "臺北市");
//        hb.addQueryParameter("elementName", "MinT");
//        final Request request = new Request.Builder().url(hb.build()).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                callback.onError();
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                Weather weather = new Gson().fromJson(response.body().string(), Weather.class);
//                if(weather.getSuccess().equals("true")){
//                    callback.onSuccess(weather);
//                }else{
//                    callback.onError();
//                }
//            }
//        });
//    }
}
