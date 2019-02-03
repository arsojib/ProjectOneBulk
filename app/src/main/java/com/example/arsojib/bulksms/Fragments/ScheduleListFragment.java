package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arsojib.bulksms.Adapter.ScheduleListAdapter;
import com.example.arsojib.bulksms.Adapter.SmsListAdapter;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Listener.ClickListener;
import com.example.arsojib.bulksms.Model.Message;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class ScheduleListFragment extends Fragment {

    View view;
    FrameLayout layout;
    ImageView back;
    RecyclerView recyclerView;
    TextView alert;
    ProgressBar progressBar;

    ArrayList<Message> arrayList;
    ScheduleListAdapter scheduleListAdapter;
    DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_list_layout, container, false);

        initialComponent();
        getData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        scheduleListAdapter = new ScheduleListAdapter(getActivity(), arrayList);
        databaseHelper = new DatabaseHelper(getActivity());
        layout = view.findViewById(R.id.layout);
        back = view.findViewById(R.id.back);
        alert = view.findViewById(R.id.alert_text);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(scheduleListAdapter);
    }

    private void getData() {
        arrayList.addAll(databaseHelper.getAllSchedule());
        notifyChange();
    }

    private void notifyChange() {
        scheduleListAdapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            alert.setVisibility(View.VISIBLE);
        } else {
            alert.setVisibility(View.GONE);
        }
    }


}
