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

package com.example.android.architecture.blueprints.todoapp.tasks;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.Location;
import com.example.android.architecture.blueprints.todoapp.data.Weather;
import com.example.android.architecture.blueprints.todoapp.data.WeatherCell;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.WeatherLocation;
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    public interface OnRecyclerViewClickListener {
        void onItemClickListener(View view);
    }

    private TasksContract.Presenter mPresenter;

    private WeatherAdapter mWeatherAdapter;

    private RecyclerView mRvWeather;

    public TasksFragment() {
        // Requires empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<WeatherCell> list = new ArrayList<>();
        mWeatherAdapter = new WeatherAdapter(list);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag, container, false);
        mRvWeather = root.findViewById(R.id.rvContainer);
        mRvWeather.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvWeather.setAdapter(mWeatherAdapter);
        mWeatherAdapter.setItemClickListener(mListener);

        return root;
    }

    private OnRecyclerViewClickListener mListener = new OnRecyclerViewClickListener() {
        @Override
        public void onItemClickListener(View view) {
            int position = mRvWeather.getChildAdapterPosition(view);
            WeatherCell cell = mWeatherAdapter.getItem(position);
            Bundle bundle = new Bundle();
            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
            bundle.putSerializable(TaskDetailActivity.EXTRA_WEATHER_INFO, cell);
            bundle.putSerializable(TaskDetailActivity.EXTRA_TASK_ID, "1");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    public void showWeathers(final Location location) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WeatherLocation.WeatherElement[] ele = new Gson().fromJson(location.getWeatherJSON(), WeatherLocation.WeatherElement[].class);
                WeatherLocation.Time[] time = ele[0].getTime();
                ArrayList<WeatherCell> list = new ArrayList<>();
                for(int i = 0; i < time.length; i++){
                    list.add(new WeatherCell(WeatherCell.CellType.TEXT.ordinal(), time[i]));
                    list.add(new WeatherCell(WeatherCell.CellType.IMAGE.ordinal(), null));
                }
                mWeatherAdapter.replaceData(list);
            }
        });
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private static class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private OnRecyclerViewClickListener mListener;
        private ArrayList<WeatherCell> mList;

        public WeatherAdapter(ArrayList<WeatherCell> list) {
            this.mList = list;
        }

        public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
            this.mListener = itemClickListener;
        }

        public void replaceData(ArrayList<WeatherCell> tasks) {
            this.mList = tasks;
            notifyDataSetChanged();
        }

        public WeatherCell getItem(int position){
            return this.mList.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            return this.mList.get(position).getType();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View vImage = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_image_item, parent, false);
            View vText = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_text_item, parent, false);

            if(viewType == WeatherCell.CellType.TEXT.ordinal()){
                return new LayoutTextHolder(vText);
            }else{
                return new LayoutImageHolder(vImage);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder.getItemViewType() == WeatherCell.CellType.TEXT.ordinal()){
                ((LayoutTextHolder)holder).bind(this.mList.get(position));
            }else{
                ((LayoutImageHolder)holder).bind(this.mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        class LayoutTextHolder extends RecyclerView.ViewHolder {
            private View view;

            private LayoutTextHolder(View view) {
                super(view);
                this.view = view;
                this.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClickListener(view);
                    }
                });
            }
            private void bind(WeatherCell data){
                ((TextView)view.findViewById(R.id.tvStartTime)).setText(data.getWeatherTime().getStartTime());
                ((TextView)view.findViewById(R.id.tvEndTime)).setText(data.getWeatherTime().getEndTime());
                ((TextView)view.findViewById(R.id.tvDetail)).setText(data.getWeatherTime().getParameter().getParameterName() + data.getWeatherTime().getParameter().getParameterUnit());
            }
        }

        class LayoutImageHolder extends RecyclerView.ViewHolder {
            private View view;
            private LayoutImageHolder(View view) {
                super(view);
                this.view = view;
                this.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClickListener(view);
                    }
                });
            }
            private void bind(WeatherCell data){
                ((ImageView)view.findViewById(R.id.ivImage)).setBackgroundResource(R.mipmap.ic_launcher);
            }
        }
    }
}
