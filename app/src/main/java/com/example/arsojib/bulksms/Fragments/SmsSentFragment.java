package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arsojib.bulksms.Adapter.SmsSentAdapter;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

public class SmsSentFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView alert;

    ArrayList<Contact> arrayList;
    SmsSentAdapter smsSentAdapter;
    DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sms_sent_layout, container, false);

        initialComponent();
        getData();

        return view;
    }

    private void getData() {
        arrayList.addAll(databaseHelper.getAllNumberUsingMessageID(Util.smsId, 1));
        notifyChange();
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        smsSentAdapter = new SmsSentAdapter(getActivity(), arrayList);
        databaseHelper = new DatabaseHelper(getActivity());
        progressBar = view.findViewById(R.id.progress_bar);
        alert = view.findViewById(R.id.alert_text);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(smsSentAdapter);
    }

    private void notifyChange() {
        smsSentAdapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            alert.setVisibility(View.VISIBLE);
        } else {
            alert.setVisibility(View.GONE);
        }
    }

}
